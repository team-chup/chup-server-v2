package team.themoment.thup.domain.dashboard.dto.response;

import java.util.List;

public record StudentDashboardResDto(long openJobs, long myApplications, long passed,
                                      List<RecommendedJobResDto> recommendedJobs) {
}