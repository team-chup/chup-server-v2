package team.themoment.thup.domain.auth.dto.response;

import team.themoment.thup.domain.user.entity.constant.Role;

public record MyAuthInfoResDto(Role role, boolean approved) {
}