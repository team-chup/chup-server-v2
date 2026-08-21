package team.themoment.thup.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient;
import team.themoment.datagsm.sdk.oauth.exception.DataGsmException;
import team.themoment.datagsm.sdk.oauth.model.Student;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.config.DataGsmOAuthProperties;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DataGsmOAuthCallbackService {

    private final DataGsmOAuthClient dataGsmOAuthClient;
    private final DataGsmOAuthProperties dataGsmOAuthProperties;
    private final UserRepository userRepository;

    public String execute(String code, String state, String stateCookie, String redirectOriginCookie, HttpServletRequest request) {
        String redirectOrigin = dataGsmOAuthProperties.isAllowedOrigin(redirectOriginCookie)
                ? redirectOriginCookie
                : dataGsmOAuthProperties.defaultRedirectOrigin();

        try {
            if (state == null || !state.equals(stateCookie)) {
                throw new ExpectedException("유효하지 않은 state 값입니다.", HttpStatus.BAD_REQUEST);
            }

            TokenResponse tokenResponse = dataGsmOAuthClient.exchangeCodeForToken(code, dataGsmOAuthProperties.redirectUri());
            UserInfo userInfo = dataGsmOAuthClient.getUserInfo(tokenResponse.getAccessToken());

            if (!userInfo.isStudent() || userInfo.getStudent() == null) {
                throw new ExpectedException("학생만 로그인할 수 있습니다.", HttpStatus.FORBIDDEN);
            }

            UserJpaEntity user = getOrCreateUser(userInfo);
            authenticate(user, request);

            return redirectOrigin + dataGsmOAuthProperties.successRedirectPath();
        } catch (DataGsmException | ExpectedException e) {
            log.warn("DataGSM OAuth 로그인 실패: {}", e.getMessage());
            return redirectOrigin + dataGsmOAuthProperties.failureRedirectPath();
        }
    }

    private UserJpaEntity getOrCreateUser(UserInfo userInfo) {
        Student student = userInfo.getStudent();
        return userRepository.findByEmail(userInfo.getEmail())
                .map(existing -> {
                    // 진급 등으로 학년이 바뀔 수 있어 로그인할 때마다 최신 값으로 갱신한다
                    existing.updateGrade(student.getGrade());
                    return existing;
                })
                .orElseGet(() -> userRepository.save(
                        UserJpaEntity.builder()
                                .email(userInfo.getEmail())
                                .name(student.getName())
                                .studentId(String.valueOf(student.getStudentNumber()))
                                .grade(student.getGrade())
                                .role(Role.STUDENT)
                                .approved(true)
                                .build()
                ));
    }

    private void authenticate(UserJpaEntity user, HttpServletRequest request) {
        // 관리자로 지정만 되고 아직 승인되지 않은 계정은 승인 전까지 관리자 권한을 주지 않는다
        Role effectiveRole = (user.getRole() == Role.ADMIN && !user.isApproved()) ? Role.STUDENT : user.getRole();

        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(effectiveRole.getAuthority())),
                Map.of(
                        "id", user.getId(),
                        "role", user.getRole().name(),
                        "email", user.getEmail(),
                        "name", user.getName()
                ),
                "id"
        );

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                oAuth2User.getAuthorities(),
                "datagsm"
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}