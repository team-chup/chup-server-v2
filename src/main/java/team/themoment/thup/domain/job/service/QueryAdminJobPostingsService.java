package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAdminJobPostingsService {

    private final JobPostingRepository jobPostingRepository;

    public List<JobPostingJpaEntity> execute(String q) {
        if (q == null || q.isBlank()) {
            return jobPostingRepository.findAll();
        }
        return jobPostingRepository.findAllByCompanyNameContainingIgnoreCase(q.trim());
    }
}