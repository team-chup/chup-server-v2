package team.themoment.thup.domain.job.dto;

import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;

import java.time.LocalDate;
import java.util.List;

public record JobPostingSummaryResponse(
        Long id,
        String companyName,
        EmploymentType employmentType,
        LocalDate recruitEnd,
        List<PositionResponse> positions
) {
    public static JobPostingSummaryResponse of(JobPostingJpaEntity jobPosting, List<JobPositionJpaEntity> positions) {
        return new JobPostingSummaryResponse(
                jobPosting.getId(),
                jobPosting.getCompanyName(),
                jobPosting.getEmploymentType(),
                jobPosting.getRecruitEnd(),
                positions.stream().map(PositionResponse::from).toList()
        );
    }
}