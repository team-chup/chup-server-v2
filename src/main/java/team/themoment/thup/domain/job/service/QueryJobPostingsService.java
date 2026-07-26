package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryJobPostingsService {

    private static final String SORT_DEADLINE_ASC = "deadline_asc";

    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;

    public List<JobPostingJpaEntity> execute(String q, String position, EmploymentType employmentType, String sort) {
        List<JobPostingJpaEntity> jobPostings = jobPostingRepository.findAllByStatus(JobPostingStatus.RECRUITING);

        Map<Long, List<String>> positionNamesByJobId = jobPositionRepository
                .findAllByJobPosting_IdIn(jobPostings.stream().map(JobPostingJpaEntity::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(
                        p -> p.getJobPosting().getId(),
                        Collectors.mapping(JobPositionJpaEntity::getName, Collectors.toList())
                ));

        String keyword = normalize(q);
        String positionFilter = normalize(position);

        List<JobPostingJpaEntity> filtered = jobPostings.stream()
                .filter(job -> employmentType == null || job.getEmploymentType() == employmentType)
                .filter(job -> positionFilter == null || positionNamesByJobId.getOrDefault(job.getId(), List.of())
                        .stream().anyMatch(name -> name.equalsIgnoreCase(positionFilter)))
                .filter(job -> keyword == null
                        || job.getCompanyName().toLowerCase().contains(keyword)
                        || positionNamesByJobId.getOrDefault(job.getId(), List.of())
                                .stream().anyMatch(name -> name.toLowerCase().contains(keyword)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (SORT_DEADLINE_ASC.equalsIgnoreCase(sort)) {
            filtered.sort(Comparator.comparing(JobPostingJpaEntity::getRecruitEnd));
        }

        return filtered;
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim().toLowerCase();
    }
}