package team.themoment.thup.domain.application.dto;

import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationSource;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;

public record AdminApplicantResponse(
        Long id,
        String name,
        String studentId,
        String email,
        String phoneNumber,
        String companyName,
        String positionName,
        ApplicationSource applicationSource,
        String sourcePlatform,
        boolean isExternal,
        ApplicationStatus status,
        LocalDateTime interviewAt
) {
    public static AdminApplicantResponse from(ApplicationJpaEntity application) {
        UserJpaEntity user = application.getUser();
        return new AdminApplicantResponse(
                application.getId(),
                user.getName(),
                user.getStudentId(),
                user.getEmail(),
                user.getPhoneNumber(),
                application.getEffectiveCompanyName(),
                application.getEffectivePositionName(),
                application.getApplicationSource(),
                application.getSourcePlatform(),
                application.isExternal(),
                application.getStatus(),
                application.getInterviewAt()
        );
    }
}
