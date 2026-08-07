package team.themoment.thup.global.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "discord")
public record DiscordProperties(
        String envLabel,
        String jobWebhookUrl,
        String errorWebhookUrl,
        String clientJobsUrl,
        Duration errorCooldown
) {
}
