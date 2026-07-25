package team.themoment.thup.global.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "session.cookie")
public record SessionCookieProperties(
        boolean secure,
        @NotBlank String sameSite
) {
}