package team.themoment.thup.domain.job.dto;

import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;

public record PositionResponse(Long id, String name) {
    public static PositionResponse from(JobPositionJpaEntity position) {
        return new PositionResponse(position.getId(), position.getName());
    }
}
