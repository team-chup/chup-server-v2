package team.themoment.thup.domain.transportsubsidy.dto;

import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;

import java.time.LocalDateTime;
import java.util.List;

public record MyTransportSubsidyResponse(
        Long id,
        String companyName,
        LocalDateTime interviewAt,
        TransportSubsidyStatus status,
        List<EvidenceFileResponse> evidences,
        LocalDateTime appliedAt,
        LocalDateTime resultUpdatedAt
) {
    public static MyTransportSubsidyResponse from(TransportSubsidyApplicationJpaEntity application,
                                                    List<TransportSubsidyEvidenceJpaEntity> evidences) {
        return new MyTransportSubsidyResponse(
                application.getId(),
                application.getCompanyName(),
                application.getInterviewAt(),
                application.getStatus(),
                evidences.stream().map(EvidenceFileResponse::from).toList(),
                application.getAppliedAt(),
                application.getResultUpdatedAt()
        );
    }
}
