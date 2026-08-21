package team.themoment.thup.domain.transportsubsidy.dto;

import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;

public record EvidenceFileResponse(
        Long id,
        String fileName,
        Integer fileSize
) {
    public static EvidenceFileResponse from(TransportSubsidyEvidenceJpaEntity evidence) {
        return new EvidenceFileResponse(evidence.getId(), evidence.getFileName(), evidence.getFileSize());
    }
}
