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
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterMyExternalApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationResponse execute(OAuth2User user, String companyName, String sourcePlatform, LocalDateTime interviewAt) {
        if (companyName == null || companyName.isBlank()) {
            throw new ExpectedException("회사명을 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (sourcePlatform == null || sourcePlatform.isBlank()) {
            throw new ExpectedException("지원 경로(플랫폼)를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (interviewAt == null) {
            throw new ExpectedException("면접 일시를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }

        Long userId = ((Number) user.getAttribute("id")).longValue();
        UserJpaEntity student = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (student.getRole() != Role.STUDENT) {
            throw new ExpectedException("학생만 외부 지원 내역을 등록할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        // 학생이 외부 플랫폼 지원 내역을 직접 등록하는 시점은 이미 서류 합격 후 면접 단계에 접어든
        // 경우를 전제로 하므로, 등록 즉시 상태를 면접 예정(INTERVIEW_SCHEDULED)으로 설정한다.
        ApplicationJpaEntity application = ApplicationJpaEntity.builder()
                .user(student)
                .companyName(companyName)
                .applicationSource(ApplicationSource.EXTERNAL)
                .sourcePlatform(sourcePlatform)
                .build();
        application.updateStatus(ApplicationStatus.INTERVIEW_SCHEDULED, interviewAt);
        applicationRepository.save(application);

        return ApplicationResponse.from(application);
    }
}