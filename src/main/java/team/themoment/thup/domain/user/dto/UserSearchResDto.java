package team.themoment.thup.domain.user.dto;

import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;

public record UserSearchResDto(
        Long id,
        String name,
        String studentId,
        String email,
        Role role
) {

    public static UserSearchResDto of(UserJpaEntity user) {
        return new UserSearchResDto(
                user.getId(),
                user.getName(),
                user.getStudentId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
