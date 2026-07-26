package team.themoment.thup.global.mail;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResendMailService implements MailService {

    private final Resend resend;
    private final MailProperties mailProperties;

    @Override
    @Async
    public void send(String to, String subject, String htmlBody) {
        if (mailProperties.apiKey() == null || mailProperties.apiKey().isBlank()) {
            log.warn("RESEND_API_KEY가 설정되지 않아 이메일 발송을 건너뜁니다. to={}, subject={}", to, subject);
            return;
        }

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(mailProperties.fromAddress())
                .to(to)
                .subject(subject)
                .html(htmlBody)
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            log.warn("이메일 발송 실패: to={}, subject={}, error={}", to, subject, e.getMessage());
        }
    }
}
