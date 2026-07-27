package team.themoment.thup.domain.job.dto;

import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record JobPostingDetailResponse(
        Long id,
        String companyName,
        String description,
        EmploymentType employmentType,
        LocalDate recruitStart,
        LocalDate recruitEnd,
        JobPostingStatus status,
        LocalDateTime createdAt,
        List<PositionResponse> positions,
        List<AttachmentResponse> attachments
) {

    public static JobPostingDetailResponse of(JobPostingJpaEntity jobPosting,
                                               List<JobPositionJpaEntity> positions,
                                               List<AttachmentJpaEntity> attachments) {
        return new JobPostingDetailResponse(
                jobPosting.getId(),
                jobPosting.getCompanyName(),
                jobPosting.getDescription(),
                jobPosting.getEmploymentType(),
                jobPosting.getRecruitStart(),
                jobPosting.getRecruitEnd(),
                jobPosting.getStatus(),
                jobPosting.getCreatedAt(),
                positions.stream().map(PositionResponse::from).toList(),
                attachments.stream().map(AttachmentResponse::from).toList()
        );
    }

    public record PositionResponse(Long id, String name) {
        public static PositionResponse from(JobPositionJpaEntity position) {
            return new PositionResponse(position.getId(), position.getName());
        }
    }

    public record AttachmentResponse(Long id, String fileName) {
        public static AttachmentResponse from(AttachmentJpaEntity attachment) {
            return new AttachmentResponse(attachment.getId(), attachment.getFileName());
        }
    }
}
