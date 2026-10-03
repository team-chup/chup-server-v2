package team.themoment.thup.domain.notice.dto;

import team.themoment.thup.domain.notice.entity.NoticeJpaEntity;

import java.time.LocalDateTime;

public record NoticeSummaryResponse(
        Long id,
        String title,
        LocalDateTime createdAt
) {
    public static NoticeSummaryResponse from(NoticeJpaEntity notice) {
        return new NoticeSummaryResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getCreatedAt()
        );
    }
}