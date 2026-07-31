package team.themoment.thup.domain.user.entity.constant;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    STUDENT,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}