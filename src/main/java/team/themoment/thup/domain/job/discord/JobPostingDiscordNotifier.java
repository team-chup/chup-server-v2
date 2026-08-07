package team.themoment.thup.domain.job.discord;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.themoment.thup.global.discord.DiscordProperties;
import team.themoment.thup.global.discord.DiscordWebhookClient;

@Component
@RequiredArgsConstructor
public class JobPostingDiscordNotifier {

    private final DiscordWebhookClient discordWebhookClient;
    private final DiscordProperties discordProperties;

    public void notifyCreated(JobPostingNotification notification) {
        discordWebhookClient.send(
                discordProperties.jobWebhookUrl(),
                JobPostingDiscordTemplate.build(
                        notification,
                        discordProperties.envLabel(),
                        discordProperties.clientJobsUrl()
                )
        );
    }
}
