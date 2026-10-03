package team.themoment.thup.domain.notice.discord;

/**
 * 공지사항 등록 알림에 필요한 값만 담은 스냅샷.
 * 알림은 다른 스레드(@Async)에서 발송되는데, NoticeJpaEntity.createdBy가 LAZY라
 * 엔티티를 그대로 넘기면 LazyInitializationException이 난다. 트랜잭션 스레드에서 값을 뽑아 넘긴다.
 */
public record NoticeNotification(
        Long noticeId,
        String title,
        String content,
        String createdByName
) {
}
