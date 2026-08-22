package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.config.DevToolsProperties;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SwitchUserRoleService {

    private final UserRepository userRepository;
    private final DevToolsProperties devToolsProperties;

    public UserJpaEntity execute(OAuth2User admin, Long userId, Role role) {
        if (!devToolsProperties.enabled()) {
            throw new ExpectedException("개발 환경에서만 사용할 수 있습니다.", HttpStatus.NOT_FOUND);
        }
        if (role == null) {
            throw new ExpectedException("변경할 역할을 선택해야 합니다.", HttpStatus.BAD_REQUEST);
        }

        Long adminId = ((Number) admin.getAttribute("id")).longValue();

        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        user.switchRole(role);
        try {
            userRepository.saveAndFlush(user);
        } catch (OptimisticLockingFailureException e) {
            throw new ExpectedException("이미 다른 요청에 의해 처리된 사용자입니다.", HttpStatus.CONFLICT);
        }

        log.info("[DEV] 역할 강제 전환: actorId={} targetUserId={} role={}", adminId, userId, role);
        return user;
    }
}
