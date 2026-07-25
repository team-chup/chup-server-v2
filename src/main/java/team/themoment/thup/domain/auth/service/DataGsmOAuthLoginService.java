package team.themoment.thup.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient;
import team.themoment.datagsm.sdk.oauth.model.AuthorizationUrlBuilder;
import team.themoment.thup.domain.auth.dto.AuthorizationUrlResult;
import team.themoment.thup.global.config.DataGsmOAuthProperties;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataGsmOAuthLoginService {

    private final DataGsmOAuthClient dataGsmOAuthClient;
    private final DataGsmOAuthProperties dataGsmOAuthProperties;

    public AuthorizationUrlResult execute() {
        String state = UUID.randomUUID().toString();
        AuthorizationUrlBuilder builder = dataGsmOAuthClient
                .createAuthorizationUrl(dataGsmOAuthProperties.redirectUri())
                .state(state);
        return new AuthorizationUrlResult(builder.build(), state);
    }
}