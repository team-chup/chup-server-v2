package team.themoment.thup.domain.application.dto;

import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
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
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static AdminApplicantResponse from(ApplicationJpaEntity application) {
        UserJpaEntity user = application.getUser();
        return new AdminApplicantResponse(
                application.getId(),
                user.getName(),
                user.getStudentId(),
                user.getEmail(),
                user.getPhoneNumber(),
                application.getJobPosting().getCompanyName(),
                application.getJobPosition().getName(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}