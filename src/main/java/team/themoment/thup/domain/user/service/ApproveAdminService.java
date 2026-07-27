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
public class ApproveAdminService {

    private final UserRepository userRepository;

    public UserJpaEntity execute(Long userId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new ExpectedException("관리자 승인 대상이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        if (user.isApproved()) {
            throw new ExpectedException("이미 승인된 관리자입니다.", HttpStatus.CONFLICT);
        }

        user.approve();
        return user;
    }
}
