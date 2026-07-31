package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.job.dto.AdminJobPostingResponse;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAdminJobPostingsService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final ApplicationRepository applicationRepository;

    public List<AdminJobPostingResponse> execute(String q) {
        List<JobPostingJpaEntity> jobPostings = (q == null || q.isBlank())
                ? jobPostingRepository.findAll()
                : jobPostingRepository.findAllByCompanyNameContainingIgnoreCase(q.trim());

        List<Long> jobPostingIds = jobPostings.stream().map(JobPostingJpaEntity::getId).toList();

        Map<Long, Long> applicantCountByJobId = applicationRepository
                .countGroupedByJobPostingIdIn(jobPostingIds)
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, List<JobPositionJpaEntity>> positionsByJobId = jobPositionRepository
                .findAllByJobPosting_IdIn(jobPostingIds)
                .stream()
                .collect(Collectors.groupingBy(p -> p.getJobPosting().getId()));

        return jobPostings.stream()
                .map(jobPosting -> AdminJobPostingResponse.of(
                        jobPosting,
                        applicantCountByJobId.getOrDefault(jobPosting.getId(), 0L),
                        positionsByJobId.getOrDefault(jobPosting.getId(), List.of())
                ))
                .toList();
    }
}