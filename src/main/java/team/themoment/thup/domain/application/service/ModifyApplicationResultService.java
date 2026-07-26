package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.mail.ApplicationEmailTemplates;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.global.mail.MailService;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyApplicationResultService {

    private final ApplicationRepository applicationRepository;
    private final MailService mailService;

    public ApplicationJpaEntity execute(Long applicationId, ApplicationStatus status) {
        ApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("지원 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        application.updateStatus(status);

        String toEmail = application.getUser().getEmail();
        String companyName = application.getJobPosting().getCompanyName();
        String positionName = application.getJobPosition().getName();

        mailService.send(
                toEmail,
                ApplicationEmailTemplates.resultSubject(companyName, status),
                ApplicationEmailTemplates.resultBody(companyName, positionName, status)
        );

        return application;
    }
}
