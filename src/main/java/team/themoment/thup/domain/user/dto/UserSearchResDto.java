package team.themoment.thup.domain.user.dto;

import team.themoment.thup.domain.user.entity.UserJpaEntity;

public record UserSearchResDto(
        Long id,
        String name,
        String studentId,
        String email
) {

    public static UserSearchResDto of(UserJpaEntity user) {
        return new UserSearchResDto(
                user.getId(),
                user.getName(),
                user.getStudentId(),
                user.getEmail()
        );
    }
}
