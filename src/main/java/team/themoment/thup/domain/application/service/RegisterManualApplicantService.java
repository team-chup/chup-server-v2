package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.dto.AdminApplicantResponse;
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
public class RegisterManualApplicantService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public AdminApplicantResponse execute(Long userId, String companyName, String sourcePlatform,
                                           ApplicationStatus status, LocalDateTime interviewAt) {
        if (userId == null) {
            throw new ExpectedException("학생을 선택해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (companyName == null || companyName.isBlank()) {
            throw new ExpectedException("회사명을 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (sourcePlatform == null || sourcePlatform.isBlank()) {
            throw new ExpectedException("지원 경로(플랫폼)를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (status == null) {
            throw new ExpectedException("현재 상태를 선택해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (status == ApplicationStatus.INTERVIEW_SCHEDULED && interviewAt == null) {
            throw new ExpectedException("면접 예정 상태로 등록하려면 면접 일시를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }

        UserJpaEntity student = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("학생을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (student.getRole() != Role.STUDENT) {
            throw new ExpectedException("학생 계정만 등록할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        ApplicationJpaEntity application = ApplicationJpaEntity.builder()
                .user(student)
                .companyName(companyName)
                .applicationSource(ApplicationSource.EXTERNAL)
                .sourcePlatform(sourcePlatform)
                .build();

        if (status != ApplicationStatus.APPLIED) {
            application.updateStatus(status, interviewAt);
        }

        applicationRepository.save(application);

        return AdminApplicantResponse.from(application);
    }
}
