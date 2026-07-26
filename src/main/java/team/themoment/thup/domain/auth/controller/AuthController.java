package team.themoment.thup.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String STATE_COOKIE_NAME = "datagsm_oauth_state";
    private static final int STATE_COOKIE_MAX_AGE_SECONDS = 300;

    private final DataGsmOAuthLoginService dataGsmOAuthLoginService;
    private final DataGsmOAuthCallbackService dataGsmOAuthCallbackService;
    private final QueryMyAuthInfoService queryMyAuthInfoService;
    private final LogoutService logoutService;

    @Operation(summary = "DataGSM OAuth 로그인 시작", description = "DataGSM 로그인 페이지로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "DataGSM 인가 페이지로 리다이렉트")
    })
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

    @Operation(summary = "DataGSM OAuth 콜백", description = "code로 토큰을 교환하고, 최초 로그인 시 자동 회원가입 후 세션을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "로그인 성공/실패 리다이렉트")
    })
    @GetMapping("/datagsm/callback")
    public void callback(@RequestParam String code,
                          @RequestParam String state,
                          @CookieValue(name = STATE_COOKIE_NAME, required = false) String stateCookie,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        String redirectUrl = dataGsmOAuthCallbackService.execute(code, state, stateCookie, request);
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "내 계정 정보 조회", description = "로그인한 사용자의 role 및 관리자 승인 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public MyAuthInfoResDto me(@AuthRequest Long userId) {
        return queryMyAuthInfoService.execute(userId);
    }

    @Operation(summary = "로그아웃", description = "현재 세션을 무효화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        logoutService.execute(request);
    }
}
