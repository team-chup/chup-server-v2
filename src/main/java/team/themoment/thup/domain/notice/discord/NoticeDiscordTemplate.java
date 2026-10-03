package team.themoment.thup.domain.notice.discord;

import org.springframework.web.util.UriComponentsBuilder;
import team.themoment.thup.global.discord.DiscordEmbed;

import java.time.Instant;
import java.util.List;

public final class NoticeDiscordTemplate {

    private static final int COLOR = 0x57F287;
    private static final int MAX_CONTENT_LENGTH = 300;

    private NoticeDiscordTemplate() {
    }

    public static DiscordEmbed build(NoticeNotification notification, String envLabel, String noticesUrl) {
        // 클라이언트 /notices 페이지가 noticeId 쿼리 파라미터로 해당 공지사항을 바로 연다
        String noticeDetailUrl = UriComponentsBuilder.fromUriString(noticesUrl)
                .queryParam("noticeId", notification.noticeId())
                .toUriString();

        return new DiscordEmbed(
                "📌 새로운 취업 공지사항이 등록되었어요!",
                null,
                noticeDetailUrl,
                COLOR,
                List.of(
                        DiscordEmbed.Field.of("📝 제목", notification.title()),
                        DiscordEmbed.Field.of("📄 내용",
                                DiscordEmbed.truncate(notification.content(), MAX_CONTENT_LENGTH)),
                        DiscordEmbed.Field.of("🔗 링크", "[공지사항 바로가기](" + noticeDetailUrl + ")")
                ),
                new DiscordEmbed.Footer("[" + envLabel + "] 작성자: " + notification.createdByName()),
                Instant.now().toString()
        );
    }
}
