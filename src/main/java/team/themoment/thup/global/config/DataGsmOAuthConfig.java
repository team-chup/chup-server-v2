package team.themoment.thup.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient;

@Configuration
@RequiredArgsConstructor
public class DataGsmOAuthConfig {

    private final DataGsmOAuthProperties dataGsmOAuthProperties;

    @Bean
    public DataGsmOAuthClient dataGsmOAuthClient() {
        return DataGsmOAuthClient.builder(
                dataGsmOAuthProperties.clientId(),
                dataGsmOAuthProperties.clientSecret()
        ).build();
    }
}