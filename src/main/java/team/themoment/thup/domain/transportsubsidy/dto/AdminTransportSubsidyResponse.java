package team.themoment.thup.domain.transportsubsidy.dto;

import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;
import team.themoment.thup.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

public record AdminTransportSubsidyResponse(
        Long id,
        String studentName,
        String studentId,
        String companyName,
        LocalDateTime interviewAt,
        TransportSubsidyStatus status,
        List<EvidenceFileResponse> evidences,
        LocalDateTime appliedAt,
        LocalDateTime resultUpdatedAt
) {
    public static AdminTransportSubsidyResponse from(TransportSubsidyApplicationJpaEntity application,
                                                       List<TransportSubsidyEvidenceJpaEntity> evidences) {
        UserJpaEntity user = application.getUser();
        return new AdminTransportSubsidyResponse(
                application.getId(),
                user.getName(),
                user.getStudentId(),
                application.getCompanyName(),
                application.getInterviewAt(),
                application.getStatus(),
                evidences.stream().map(EvidenceFileResponse::from).toList(),
                application.getAppliedAt(),
                application.getResultUpdatedAt()
        );
    }
}
