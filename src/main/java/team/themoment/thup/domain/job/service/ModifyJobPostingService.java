package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyJobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public JobPostingJpaEntity execute(Long jobId, String companyName, String description,
                                        EmploymentType employmentType, LocalDate recruitStart, LocalDate recruitEnd) {
        JobPostingJpaEntity jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        jobPosting.update(companyName, description, employmentType, recruitStart, recruitEnd);
        return jobPosting;
    }
}