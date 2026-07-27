package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoteToAdminService {

    private final UserRepository userRepository;

    public UserJpaEntity execute(Long userId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (user.getRole() == Role.ADMIN) {
            throw new ExpectedException("이미 관리자인 사용자입니다.", HttpStatus.CONFLICT);
        }

        user.promoteToAdmin();
        return user;
    }
}
