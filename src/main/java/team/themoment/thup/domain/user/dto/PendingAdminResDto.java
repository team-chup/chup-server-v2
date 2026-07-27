package team.themoment.thup.domain.user.dto;

import team.themoment.thup.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;

public record PendingAdminResDto(
        Long id,
        String name,
        String email,
        String studentId,
        LocalDateTime createdAt
) {

    public static PendingAdminResDto of(UserJpaEntity user) {
        return new PendingAdminResDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStudentId(),
                user.getCreatedAt()
        );
    }
}
