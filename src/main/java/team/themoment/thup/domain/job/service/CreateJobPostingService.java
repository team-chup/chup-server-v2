package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.AttachmentFileType;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.repository.AttachmentRepository;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;
import team.themoment.thup.domain.job.util.AttachmentFileTypeResolver;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.StoredFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateJobPostingService {

    private static final int MAX_ATTACHMENTS = 5;
    private static final long MAX_ATTACHMENT_SIZE_BYTES = 10L * 1024 * 1024;

    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public JobPostingJpaEntity execute(OAuth2User admin, String companyName, String description,
                                        EmploymentType employmentType, LocalDate recruitStart, LocalDate recruitEnd,
                                        List<String> positionNames, List<MultipartFile> attachments) {
        validateAttachments(attachments);

        Long adminId = ((Number) admin.getAttribute("id")).longValue();
        UserJpaEntity createdBy = userRepository.findById(adminId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        JobPostingJpaEntity saved = jobPostingRepository.save(
                JobPostingJpaEntity.builder()
                        .createdBy(createdBy)
                        .companyName(companyName)
                        .description(description)
                        .employmentType(employmentType)
                        .recruitStart(recruitStart)
                        .recruitEnd(recruitEnd)
                        .build()
        );

        positionNames.forEach(name -> jobPositionRepository.save(
                JobPositionJpaEntity.builder()
                        .jobPosting(saved)
                        .name(name)
                        .build()
        ));

        saveAttachments(saved, attachments);

        return saved;
    }

    private void validateAttachments(List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        if (attachments.size() > MAX_ATTACHMENTS) {
            throw new ExpectedException("첨부파일은 최대 " + MAX_ATTACHMENTS + "개까지 등록할 수 있습니다.", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        for (MultipartFile file : attachments) {
            if (file.getSize() > MAX_ATTACHMENT_SIZE_BYTES) {
                throw new ExpectedException("첨부파일 용량이 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE);
            }
        }
    }

    private void saveAttachments(JobPostingJpaEntity jobPosting, List<MultipartFile> attachments) {
        if (attachments == null) {
            return;
        }
        for (MultipartFile file : attachments) {
            AttachmentFileType fileType = AttachmentFileTypeResolver.resolve(file);
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