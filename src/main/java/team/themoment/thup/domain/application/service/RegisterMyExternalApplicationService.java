package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.dto.ApplicationResponse;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationSource;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterMyExternalApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationResponse execute(OAuth2User user, String companyName, String sourcePlatform) {
        if (companyName == null || companyName.isBlank()) {
            throw new ExpectedException("회사명을 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (sourcePlatform == null || sourcePlatform.isBlank()) {
            throw new ExpectedException("지원 경로(플랫폼)를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }

        Long userId = ((Number) user.getAttribute("id")).longValue();
        UserJpaEntity student = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (student.getRole() != Role.STUDENT) {
            throw new ExpectedException("학생만 외부 지원 내역을 등록할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        // 등록 시점의 결과를 학생이 임의로 지정하지 못하도록 상태는 항상 지원 접수(APPLIED)로 시작하고,
        // 이후 상태 변경은 기존 관리자용 처리(PATCH /api/admin/applicants/{id}/result)로만 가능하다.
        ApplicationJpaEntity application = applicationRepository.save(
                ApplicationJpaEntity.builder()
                        .user(student)
                        .companyName(companyName)
                        .applicationSource(ApplicationSource.EXTERNAL)
                        .sourcePlatform(sourcePlatform)
                        .build()
        );

        return ApplicationResponse.from(application);
    }
}