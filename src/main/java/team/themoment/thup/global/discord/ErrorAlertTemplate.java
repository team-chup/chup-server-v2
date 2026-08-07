package team.themoment.thup.global.discord;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

final class ErrorAlertTemplate {

    private static final int COLOR_SERVER_ERROR = 0xE74C3C;
    private static final int COLOR_CLIENT_ERROR = 0xE67E22;
    private static final int MAX_STACK_TRACE_FRAMES = 12;
    private static final int MAX_STACK_TRACE_LENGTH = 1000;

    private ErrorAlertTemplate() {
    }

    static DiscordEmbed build(String envLabel, int status, HttpServletRequest request, Exception exception, int suppressedCount) {
        List<DiscordEmbed.Field> fields = new ArrayList<>();
        fields.add(DiscordEmbed.Field.of("🌐 요청", request.getMethod() + " " + fullPath(request)));
        fields.add(DiscordEmbed.Field.of("👤 사용자", resolveUser()));

        if (exception != null) {
            fields.add(DiscordEmbed.Field.of("💥 예외",
                    exception.getClass().getSimpleName() + ": " + exception.getMessage()));
            // 코드블록 마크업까지 포함해 1024자를 넘기지 않도록 본문을 먼저 줄인다
            fields.add(DiscordEmbed.Field.of("📄 스택트레이스",
                    "```" + DiscordEmbed.truncate(stackTrace(exception), MAX_STACK_TRACE_LENGTH) + "```"));
        }
        if (suppressedCount > 0) {
            fields.add(DiscordEmbed.Field.of("🔁 반복", "직전 발송 이후 동일 에러가 " + suppressedCount + "회 더 발생했습니다."));
        }

        return new DiscordEmbed(
                "[" + envLabel + "] 🚨 " + status + " " + reasonPhrase(status),
                null,
                null,
                status >= 500 ? COLOR_SERVER_ERROR : COLOR_CLIENT_ERROR,
                fields,
                null,
                Instant.now().toString()
        );
    }

    private static String fullPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
    }

    private static String resolveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User user)) {
            return "비로그인";
        }
        return user.getAttribute("email") + " (" + user.getAttribute("role") + ")";
    }

    private static String stackTrace(Exception exception) {
        StackTraceElement[] elements = exception.getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Math.min(elements.length, MAX_STACK_TRACE_FRAMES); i++) {
            builder.append(elements[i]).append("\n");
        }
        return builder.toString();
    }

    private static String reasonPhrase(int status) {
        HttpStatus httpStatus = HttpStatus.resolve(status);
        return httpStatus == null ? "" : httpStatus.getReasonPhrase();
    }
}
