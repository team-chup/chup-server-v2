package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryJobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public JobPostingJpaEntity execute(Long jobId) {
        return jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}