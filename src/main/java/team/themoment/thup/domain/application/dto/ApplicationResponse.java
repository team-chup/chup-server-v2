package team.themoment.thup.domain.application.dto;

import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationSource;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        String companyName,
        String positionName,
        ApplicationSource applicationSource,
        String sourcePlatform,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static ApplicationResponse from(ApplicationJpaEntity application) {
        return new ApplicationResponse(
                application.getId(),
                application.getEffectiveCompanyName(),
                application.getEffectivePositionName(),
                application.getApplicationSource(),
                application.getSourcePlatform(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
