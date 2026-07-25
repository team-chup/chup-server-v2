package team.themoment.thup.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.themoment.thup.domain.auth.dto.AuthorizationUrlResult;
import team.themoment.thup.domain.auth.dto.response.MyAuthInfoResDto;
import team.themoment.thup.domain.auth.service.DataGsmOAuthCallbackService;
import team.themoment.thup.domain.auth.service.DataGsmOAuthLoginService;
import team.themoment.thup.domain.auth.service.LogoutService;
import team.themoment.thup.domain.auth.service.QueryMyAuthInfoService;
import team.themoment.thup.global.security.annotation.AuthRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String STATE_COOKIE_NAME = "datagsm_oauth_state";
    private static final int STATE_COOKIE_MAX_AGE_SECONDS = 300;

    private final DataGsmOAuthLoginService dataGsmOAuthLoginService;
    private final DataGsmOAuthCallbackService dataGsmOAuthCallbackService;
    private final QueryMyAuthInfoService queryMyAuthInfoService;
    private final LogoutService logoutService;

    @GetMapping("/datagsm/login")
    public void login(HttpServletResponse response) throws IOException {
        AuthorizationUrlResult result = dataGsmOAuthLoginService.execute();

        Cookie stateCookie = new Cookie(STATE_COOKIE_NAME, result.state());
        stateCookie.setHttpOnly(true);
        stateCookie.setPath("/api/auth/datagsm/callback");
        stateCookie.setMaxAge(STATE_COOKIE_MAX_AGE_SECONDS);
        response.addCookie(stateCookie);

        response.sendRedirect(result.url());
    }

    @GetMapping("/datagsm/callback")
    public void callback(@RequestParam String code,
                          @RequestParam String state,
                          @CookieValue(name = STATE_COOKIE_NAME, required = false) String stateCookie,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        String redirectUrl = dataGsmOAuthCallbackService.execute(code, state, stateCookie, request);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/me")
    public MyAuthInfoResDto me(@AuthRequest Long userId) {
        return queryMyAuthInfoService.execute(userId);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        logoutService.execute(request);
    }
}
