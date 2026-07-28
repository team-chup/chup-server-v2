package team.themoment.thup.domain.application.dto;

import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        String companyName,
        String positionName,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static ApplicationResponse from(ApplicationJpaEntity application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJobPosting().getCompanyName(),
                application.getJobPosition().getName(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}