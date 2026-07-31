package team.themoment.thup.domain.job.dto;

import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AdminJobPostingResponse(
        Long id,
        String companyName,
        String description,
        EmploymentType employmentType,
        LocalDate recruitStart,
        LocalDate recruitEnd,
        JobPostingStatus status,
        LocalDateTime createdAt,
        long applicantCount,
        List<PositionResponse> positions
) {

    public static AdminJobPostingResponse of(JobPostingJpaEntity jobPosting, long applicantCount, List<JobPositionJpaEntity> positions) {
        return new AdminJobPostingResponse(
                jobPosting.getId(),
                jobPosting.getCompanyName(),
                jobPosting.getDescription(),
                jobPosting.getEmploymentType(),
                jobPosting.getRecruitStart(),
                jobPosting.getRecruitEnd(),
                jobPosting.getStatus(),
                jobPosting.getCreatedAt(),
                applicantCount,
                positions.stream().map(PositionResponse::from).toList()
        );
    }
}
