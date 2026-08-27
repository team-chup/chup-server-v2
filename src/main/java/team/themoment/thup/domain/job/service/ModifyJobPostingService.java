package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.job.dto.JobPostingDetailResponse;
import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.AttachmentFileType;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.repository.AttachmentRepository;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;
import team.themoment.thup.domain.job.util.AttachmentFileTypeResolver;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.StoredFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyJobPostingService {

    private static final int MAX_ATTACHMENTS = 5;
    private static final long MAX_ATTACHMENT_SIZE_BYTES = 20L * 1024 * 1024;

    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final AttachmentRepository attachmentRepository;
    private final ApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    public JobPostingDetailResponse execute(Long jobId, String companyName, String description,
                                             EmploymentType employmentType, LocalDate recruitStart, LocalDate recruitEnd,
                                             List<String> positionNames, List<Long> retainedAttachmentIds,
                                             List<MultipartFile> attachments) {
        JobPostingJpaEntity jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        jobPosting.update(companyName, description, employmentType, recruitStart, recruitEnd);

        if (positionNames != null) {
            updatePositions(jobPosting, positionNames);
        }

        if (retainedAttachmentIds != null || (attachments != null && !attachments.isEmpty())) {
            updateAttachments(jobPosting, retainedAttachmentIds, attachments);
        }

        List<JobPositionJpaEntity> currentPositions = jobPositionRepository.findAllByJobPosting_Id(jobId);
        List<AttachmentJpaEntity> currentAttachments = attachmentRepository.findAllByJobPosting_Id(jobId);
        return JobPostingDetailResponse.of(jobPosting, currentPositions, currentAttachments);
    }

    private void updatePositions(JobPostingJpaEntity jobPosting, List<String> positionNames) {
        Set<String> newNames = positionNames.stream()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toSet());

        List<JobPositionJpaEntity> existing = jobPositionRepository.findAllByJobPosting_Id(jobPosting.getId());
        Set<String> existingNames = existing.stream()
                .map(JobPositionJpaEntity::getName)
                .collect(Collectors.toSet());

        List<String> blockedByApplicants = new ArrayList<>();
        for (JobPositionJpaEntity position : existing) {
            if (newNames.contains(position.getName())) {
                continue;
            }
            if (applicationRepository.existsByJobPosition_Id(position.getId())) {
                blockedByApplicants.add(position.getName());
                continue;
            }
            jobPositionRepository.delete(position);
        }

        if (!blockedByApplicants.isEmpty()) {
            throw new ExpectedException(
                    "지원자가 있는 포지션은 삭제할 수 없습니다: " + String.join(", ", blockedByApplicants),
                    HttpStatus.CONFLICT
            );
        }

        newNames.stream()
                .filter(name -> !existingNames.contains(name))
                .forEach(name -> jobPositionRepository.save(
                        JobPositionJpaEntity.builder()
                                .jobPosting(jobPosting)
                                .name(name)
                                .build()
                ));
    }

    private void updateAttachments(JobPostingJpaEntity jobPosting, List<Long> retainedAttachmentIds, List<MultipartFile> attachments) {
        List<MultipartFile> newFiles = attachments == null ? List.of() : attachments;
        List<AttachmentJpaEntity> existing = attachmentRepository.findAllByJobPosting_Id(jobPosting.getId());

        // retainedAttachmentIds를 안 보내면(null) "새 파일만 추가, 기존은 그대로 유지"가 안전한 기본값이다.
        // 기존 전부를 지우고 싶으면 retainedAttachmentIds=[]를 명시적으로 보내야 한다.
        Set<Long> retainedIds = retainedAttachmentIds == null
                ? existing.stream().map(AttachmentJpaEntity::getId).collect(Collectors.toSet())
                : new HashSet<>(retainedAttachmentIds);

        List<AttachmentJpaEntity> toDelete = existing.stream()
                .filter(attachment -> !retainedIds.contains(attachment.getId()))
                .toList();
        long retainedCount = existing.size() - toDelete.size();

        if (retainedCount + newFiles.size() > MAX_ATTACHMENTS) {
            throw new ExpectedException("첨부파일은 최대 " + MAX_ATTACHMENTS + "개까지 등록할 수 있습니다.", HttpStatus.PAYLOAD_TOO_LARGE);
        }

        List<AttachmentFileType> fileTypes = new ArrayList<>(newFiles.size());
        for (MultipartFile file : newFiles) {
            if (file.getSize() > MAX_ATTACHMENT_SIZE_BYTES) {
                throw new ExpectedException("첨부파일 용량이 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE);
            }
            fileTypes.add(AttachmentFileTypeResolver.resolve(file));
        }

        attachmentRepository.deleteAll(toDelete);
        toDelete.forEach(attachment -> fileStorageService.delete(attachment.getFileUrl()));

        for (int i = 0; i < newFiles.size(); i++) {
            MultipartFile file = newFiles.get(i);
            AttachmentFileType fileType = fileTypes.get(i);
            StoredFile stored = fileStorageService.store(file, "job-attachments/" + jobPosting.getId());

            attachmentRepository.save(
                    AttachmentJpaEntity.builder()
                            .jobPosting(jobPosting)
                            .fileName(stored.originalFileName())
                            .fileUrl(stored.storageKey())
                            .fileType(fileType)
                            .fileSize((int) stored.size())
                            .build()
            );
        }
    }
}
