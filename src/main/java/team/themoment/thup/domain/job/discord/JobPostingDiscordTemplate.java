package team.themoment.thup.domain.job.discord;

import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.global.discord.DiscordEmbed;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class JobPostingDiscordTemplate {

    private static final int COLOR = 0x5865F2;
    private static final int MAX_DESCRIPTION_LENGTH = 300;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    private JobPostingDiscordTemplate() {
    }

    public static DiscordEmbed build(JobPostingNotification notification, String envLabel, String jobsUrl) {
        return new DiscordEmbed(
                "📢 새로운 채용 공고가 등록되었어요!",
                null,
                jobsUrl,
                COLOR,
                List.of(
                        DiscordEmbed.Field.of("🏢 회사명", notification.companyName()),
                        DiscordEmbed.Field.of("📝 회사 설명",
                                DiscordEmbed.truncate(notification.description(), MAX_DESCRIPTION_LENGTH)),
                        DiscordEmbed.Field.of("💼 고용 형태", employmentTypeLabel(notification.employmentType())),
                        DiscordEmbed.Field.of("👩‍💼 모집 포지션", String.join(", ", notification.positionNames())),
                        DiscordEmbed.Field.of("📅 모집 기간",
                                DATE_FORMATTER.format(notification.recruitStart())
                                        + " ~ " + DATE_FORMATTER.format(notification.recruitEnd())),
                        DiscordEmbed.Field.of("🔗 링크", "[채용 공고 바로가기](" + jobsUrl + ")")
                ),
                new DiscordEmbed.Footer("[" + envLabel + "] 등록자: " + notification.createdByName()),
                Instant.now().toString()
        );
    }

    private static String employmentTypeLabel(EmploymentType employmentType) {
        return switch (employmentType) {
            case FULL_TIME -> "정규직";
            case CONTRACT -> "계약직";
            case INTERN -> "인턴";
            case INDUSTRIAL_FUNCTIONAL_PERSONNEL -> "산업기능요원";
        };
    }
}
