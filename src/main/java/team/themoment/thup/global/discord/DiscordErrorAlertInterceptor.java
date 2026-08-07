package team.themoment.thup.global.discord;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 4xx/5xx 응답을 디스코드로 알린다.
 * <p>
 * 서블릿 필터가 아니라 인터셉터를 쓰는 이유: the-sdk의 LoggingFilter가 하위 체인 예외를 잡아 로그만 남기고
 * 다시 던지지 않기 때문에, 필터 위치에 따라 예외를 못 볼 수 있다. 인터셉터의 afterCompletion은
 * DispatcherServlet 안에서 실행되어 예외(미처리 시)와 최종 상태코드(ExpectedException 처리 시)를 모두 볼 수 있다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordErrorAlertInterceptor implements HandlerInterceptor {

    // 로그아웃 상태 접근(401), 권한 없음(403), 없는 경로(404)는 정상 운영 중에도 상시 발생해 알림에서 제외한다
    private static final Set<Integer> IGNORED_STATUSES = Set.of(401, 403, 404);
    private static final int MAX_ALERTS_PER_MINUTE = 20;
    private static final int MAX_TRACKED_KEYS = 500;

    private final DiscordWebhookClient discordWebhookClient;
    private final DiscordProperties discordProperties;

    private final Map<String, Suppression> suppressions = new ConcurrentHashMap<>();
    private int alertsInCurrentMinute = 0;
    private long currentMinuteStartedAt = System.currentTimeMillis();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 에러 응답이 만들어지면 서블릿이 /error로 ERROR 디스패치를 한 번 더 돌리고, 그것도 핸들러에
        // 매핑되어 있어 인터셉터가 두 번 실행된다. 원본 요청만 알린다.
        if (request.getDispatcherType() != DispatcherType.REQUEST) {
            return;
        }

        // 예외가 여기까지 온 경우는 어떤 핸들러도 처리하지 못한 상태이며, 응답 상태는 아직 200일 수 있다
        int status = ex != null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : response.getStatus();
        if (status < 400 || IGNORED_STATUSES.contains(status)) {
            return;
        }

        try {
            String key = status + " " + request.getMethod() + " " + request.getRequestURI()
                    + (ex == null ? "" : " " + ex.getClass().getName());

            int suppressedCount = consumeSuppression(key);
            if (suppressedCount < 0 || !withinRateLimit()) {
                return;
            }

            discordWebhookClient.send(
                    discordProperties.errorWebhookUrl(),
                    ErrorAlertTemplate.build(discordProperties.envLabel(), status, request, ex, suppressedCount)
            );
        } catch (Exception e) {
            log.warn("에러 알림 처리 실패: {}", e.getMessage());
        }
    }

    /**
     * 발송해야 하면 직전 발송 이후 억제된 횟수(0 이상)를, 쿨다운 중이면 -1을 반환한다.
     */
    private int consumeSuppression(String key) {
        Instant now = Instant.now();
        Duration cooldown = discordProperties.errorCooldown();
        int[] suppressedCount = new int[1];

        suppressions.compute(key, (ignored, existing) -> {
            if (existing == null || Duration.between(existing.lastSentAt(), now).compareTo(cooldown) >= 0) {
                suppressedCount[0] = existing == null ? 0 : existing.suppressedCount();
                return new Suppression(now, 0);
            }
            suppressedCount[0] = -1;
            return new Suppression(existing.lastSentAt(), existing.suppressedCount() + 1);
        });

        if (suppressions.size() > MAX_TRACKED_KEYS) {
            suppressions.entrySet().removeIf(entry ->
                    Duration.between(entry.getValue().lastSentAt(), now).compareTo(cooldown) >= 0);
        }
        return suppressedCount[0];
    }

    /**
     * 에러가 폭주할 때 디스코드 rate limit(429)에 걸리지 않도록 분당 발송 건수를 제한한다.
     */
    private synchronized boolean withinRateLimit() {
        long now = System.currentTimeMillis();
        if (now - currentMinuteStartedAt >= 60_000L) {
            currentMinuteStartedAt = now;
            alertsInCurrentMinute = 0;
        }
        return ++alertsInCurrentMinute <= MAX_ALERTS_PER_MINUTE;
    }

    private record Suppression(Instant lastSentAt, int suppressedCount) {
    }
}
