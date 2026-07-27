package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.job.dto.AdminJobPostingResponse;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAdminJobPostingsService {

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    public List<AdminJobPostingResponse> execute(String q) {
        List<JobPostingJpaEntity> jobPostings = (q == null || q.isBlank())
                ? jobPostingRepository.findAll()
                : jobPostingRepository.findAllByCompanyNameContainingIgnoreCase(q.trim());

        return jobPostings.stream()
                .map(jobPosting -> AdminJobPostingResponse.of(
                        jobPosting,
                        applicationRepository.countByJobPosting_Id(jobPosting.getId())
                ))
                .toList();
    }
}