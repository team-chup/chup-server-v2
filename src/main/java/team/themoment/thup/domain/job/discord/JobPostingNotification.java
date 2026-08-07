package team.themoment.thup.domain.job.discord;

import team.themoment.thup.domain.job.entity.constant.EmploymentType;

import java.time.LocalDate;
import java.util.List;

/**
 * 공고 등록 알림에 필요한 값만 담은 스냅샷.
 * 알림은 다른 스레드(@Async)에서 발송되는데, JobPostingJpaEntity.createdBy가 LAZY라
 * 엔티티를 그대로 넘기면 LazyInitializationException이 난다. 트랜잭션 스레드에서 값을 뽑아 넘긴다.
 */
public record JobPostingNotification(
        String companyName,
        String description,
        EmploymentType employmentType,
        List<String> positionNames,
        LocalDate recruitStart,
        LocalDate recruitEnd,
        String createdByName
) {
}
