package team.themoment.thup.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "oauth.datagsm")
public record DataGsmOAuthProperties(
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String redirectUri,
        @NotEmpty List<String> allowedRedirectOrigins,
        @NotBlank String defaultRedirectOrigin,
        String successRedirectPath,
        String failureRedirectPath
) {
    public boolean isAllowedOrigin(String origin) {
        return origin != null && allowedRedirectOrigins.contains(origin);
    }
}