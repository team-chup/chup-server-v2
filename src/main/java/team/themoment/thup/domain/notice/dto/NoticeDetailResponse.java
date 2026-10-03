package team.themoment.thup.domain.notice.dto;

import team.themoment.thup.domain.notice.entity.NoticeJpaEntity;

import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long id,
        String title,
        String content,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NoticeDetailResponse from(NoticeJpaEntity notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedBy().getName(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}