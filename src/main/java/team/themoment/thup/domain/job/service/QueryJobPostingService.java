package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.dto.JobPostingDetailResponse;
import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.AttachmentRepository;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryJobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final AttachmentRepository attachmentRepository;

    public JobPostingDetailResponse execute(Long jobId) {
        JobPostingJpaEntity jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<JobPositionJpaEntity> positions = jobPositionRepository.findAllByJobPosting_Id(jobId);
        List<AttachmentJpaEntity> attachments = attachmentRepository.findAllByJobPosting_Id(jobId);

        return JobPostingDetailResponse.of(jobPosting, positions, attachments);
    }
}