package team.themoment.thup.global.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
public record MailProperties(String apiKey, String fromAddress) {
}
