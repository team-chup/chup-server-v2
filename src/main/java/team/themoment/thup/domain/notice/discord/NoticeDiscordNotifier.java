package team.themoment.thup.domain.notice.discord;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.themoment.thup.global.discord.DiscordProperties;
import team.themoment.thup.global.discord.DiscordWebhookClient;

@Component
@RequiredArgsConstructor
public class NoticeDiscordNotifier {

    private final DiscordWebhookClient discordWebhookClient;
    private final DiscordProperties discordProperties;

    // 취업 관련 공지라 채용 공고 알림과 같은 채널(job webhook)로 보낸다
    public void notifyCreated(NoticeNotification notification) {
        discordWebhookClient.send(
                discordProperties.jobWebhookUrl(),
                NoticeDiscordTemplate.build(
                        notification,
                        discordProperties.envLabel(),
                        discordProperties.clientNoticesUrl()
                )
        );
    }
}
