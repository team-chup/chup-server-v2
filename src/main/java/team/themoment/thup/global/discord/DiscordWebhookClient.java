package team.themoment.thup.global.discord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class DiscordWebhookClient {

    private final RestClient restClient;

    public DiscordWebhookClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 타임아웃이 없으면 디스코드 응답이 늦어질 때 비동기 스레드 풀이 묶일 수 있다
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    /**
     * 알림 발송은 부가 기능이므로 실패해도 원래 요청에 영향을 주지 않는다.
     * 웹훅 URL이 비어 있으면(로컬 등) 발송을 건너뛴다.
     */
    @Async
    public void send(String webhookUrl, DiscordEmbed embed) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("디스코드 웹훅 URL이 설정되지 않아 알림 발송을 건너뜁니다. title={}", embed.title());
            return;
        }

        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Payload(List.of(embed)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("디스코드 알림 발송 실패: title={}, error={}", embed.title(), e.getMessage());
        }
    }

    private record Payload(List<DiscordEmbed> embeds) {
    }
}
