package team.themoment.thup.domain.job.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.themoment.thup.domain.job.service.CloseExpiredJobPostingsService;
import team.themoment.thup.global.time.AppTimeZone;

import java.time.LocalDate;
import java.util.List;

/**
 * 모집 기간이 지난 공고를 자동으로 마감 처리한다.
 * <p>
 * 앱 컨테이너에 TZ가 지정되어 있지 않아 JVM 기본 시간대가 UTC다. 마감 기준일이 하루 어긋나지 않도록
 * 실행 시각(cron zone)과 기준 날짜를 모두 한국 시간으로 고정한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobPostingCloseScheduler {

    private final CloseExpiredJobPostingsService closeExpiredJobPostingsService;

    @Scheduled(cron = "0 0 0 * * *", zone = AppTimeZone.KST_ID)
    public void closeExpiredJobPostings() {
        closeExpired();
    }

    /**
     * 배포·장애로 자정 배치를 놓쳤을 수 있어 기동 직후에도 한 번 보정한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void closeExpiredJobPostingsOnStartup() {
        closeExpired();
    }

    private void closeExpired() {
        LocalDate today = LocalDate.now(AppTimeZone.KST);
        List<String> closedCompanyNames = closeExpiredJobPostingsService.execute(today);
        if (!closedCompanyNames.isEmpty()) {
            log.info("모집 기간이 지난 공고 {}건을 마감 처리했습니다. (기준일: {}, 회사: {})",
                    closedCompanyNames.size(), today, String.join(", ", closedCompanyNames));
        }
    }
}
