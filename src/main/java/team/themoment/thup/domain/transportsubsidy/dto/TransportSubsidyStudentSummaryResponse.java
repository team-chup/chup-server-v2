package team.themoment.thup.domain.transportsubsidy.dto;

public record TransportSubsidyStudentSummaryResponse(
        Long userId,
        String name,
        String studentId,
        long approvedCount,
        long totalCount
) {
}
