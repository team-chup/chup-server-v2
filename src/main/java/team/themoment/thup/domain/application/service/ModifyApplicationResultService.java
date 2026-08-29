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
import team.themoment.thup.domain.application.mail.ApplicationEmailTemplates;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.global.mail.MailService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyApplicationResultService {

    private final ApplicationRepository applicationRepository;
    private final MailService mailService;

    public AdminApplicantResponse execute(Long applicationId, ApplicationStatus status, LocalDateTime interviewAt) {
        if (status == ApplicationStatus.INTERVIEW_SCHEDULED && interviewAt == null) {
            throw new ExpectedException("면접 예정 상태로 변경하려면 면접 일시를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }

        ApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("지원 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        application.updateStatus(status, interviewAt);

        if (application.getApplicationSource() == ApplicationSource.OFFICIAL) {
            String toEmail = application.getUser().getEmail();
            String companyName = application.getEffectiveCompanyName();
            String positionName = application.getEffectivePositionName();

            mailService.send(
                    toEmail,
                    ApplicationEmailTemplates.resultSubject(companyName, status),
                    ApplicationEmailTemplates.resultBody(companyName, positionName, status)
            );
        }

        return AdminApplicantResponse.from(application);
    }
}
