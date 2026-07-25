package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyJobPostingStatusService {

    private final JobPostingRepository jobPostingRepository;

    public JobPostingJpaEntity execute(Long jobId, JobPostingStatus status) {
        JobPostingJpaEntity jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        jobPosting.updateStatus(status);
        return jobPosting;
    }
}