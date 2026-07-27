package team.themoment.thup.domain.job.dto;

import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminJobPostingResponse(
        Long id,
        String companyName,
        String description,
        EmploymentType employmentType,
        LocalDate recruitStart,
        LocalDate recruitEnd,
        JobPostingStatus status,
        LocalDateTime createdAt,
        long applicantCount
) {

    public static AdminJobPostingResponse of(JobPostingJpaEntity jobPosting, long applicantCount) {
        return new AdminJobPostingResponse(
                jobPosting.getId(),
                jobPosting.getCompanyName(),
                jobPosting.getDescription(),
                jobPosting.getEmploymentType(),
                jobPosting.getRecruitStart(),
                jobPosting.getRecruitEnd(),
                jobPosting.getStatus(),
                jobPosting.getCreatedAt(),
                applicantCount
        );
    }
}
