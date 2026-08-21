package team.themoment.thup.domain.transportsubsidy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.transportsubsidy.dto.AdminTransportSubsidyResponse;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;
import team.themoment.thup.domain.transportsubsidy.mail.TransportSubsidyEmailTemplates;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyApplicationRepository;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyEvidenceRepository;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.mail.MailService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyTransportSubsidyResultService {

    private static final int MAX_APPROVED_COUNT = 2;

    private final TransportSubsidyApplicationRepository applicationRepository;
    private final TransportSubsidyEvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public AdminTransportSubsidyResponse execute(Long applicationId, TransportSubsidyStatus status) {
        if (status != TransportSubsidyStatus.APPROVED && status != TransportSubsidyStatus.REJECTED) {
            throw new ExpectedException("승인 또는 거절 상태만 지정할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        TransportSubsidyApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (application.getStatus() != TransportSubsidyStatus.PENDING) {
            throw new ExpectedException("이미 처리된 신청입니다.", HttpStatus.CONFLICT);
        }

        Long userId = application.getUser().getId();

        if (status == TransportSubsidyStatus.APPROVED) {
            // 동시 승인 요청이 카운트 체크를 동시에 통과해 승인 횟수 제한을 넘기지 않도록,
            // 유저 row에 락을 건 뒤 그 안에서 카운트를 확인한다.
            userRepository.findByIdForUpdate(userId)
                    .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

            if (applicationRepository.countByUser_IdAndStatus(userId, TransportSubsidyStatus.APPROVED) >= MAX_APPROVED_COUNT) {
                throw new ExpectedException(
                        "이미 면접 교통비 지원을 " + MAX_APPROVED_COUNT + "회 모두 사용했습니다.", HttpStatus.CONFLICT
                );
            }
        }

        application.updateStatus(status);

        mailService.send(
                application.getUser().getEmail(),
                TransportSubsidyEmailTemplates.resultSubject(application.getCompanyName(), status),
                TransportSubsidyEmailTemplates.resultBody(application.getCompanyName(), status)
        );

        List<TransportSubsidyEvidenceJpaEntity> evidences = evidenceRepository.findAllByApplication_Id(applicationId);
        return AdminTransportSubsidyResponse.from(application, evidences);
    }
}
