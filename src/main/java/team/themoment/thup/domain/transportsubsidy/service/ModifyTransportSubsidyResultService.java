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
import team.themoment.thup.global.mail.MailService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyTransportSubsidyResultService {

    private final TransportSubsidyApplicationRepository applicationRepository;
    private final TransportSubsidyEvidenceRepository evidenceRepository;
    private final MailService mailService;

    public AdminTransportSubsidyResponse execute(Long applicationId, TransportSubsidyStatus status) {
        if (status != TransportSubsidyStatus.APPROVED && status != TransportSubsidyStatus.REJECTED) {
            throw new ExpectedException("승인 또는 거절 상태만 지정할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        // 동일 신청 건에 대한 동시 승인/거절 요청이 서로의 결과를 조용히 덮어쓰지 않도록,
        // 신청 건 row에 락을 건 뒤 그 안에서 상태를 확인·변경한다.
        TransportSubsidyApplicationJpaEntity application = applicationRepository.findByIdForUpdate(applicationId)
                .orElseThrow(() -> new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (application.getStatus() != TransportSubsidyStatus.PENDING) {
            throw new ExpectedException("이미 처리된 신청입니다.", HttpStatus.CONFLICT);
        }

        // 승인 횟수 상한은 두지 않는다. 잔여 예산 범위 내 추가 지원 여부는 선생님의 승인 판단에 맡기고,
        // 누적 횟수는 QueryTransportSubsidyStudentsService에서 관리 목적으로 계속 집계한다.
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
