package team.themoment.thup.domain.application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import team.themoment.thup.domain.application.entity.constant.ApplicationSource;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_application", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "job_posting_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id")
    private JobPostingJpaEntity jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_position_id")
    private JobPositionJpaEntity jobPosition;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_source", nullable = false, columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'OFFICIAL'")
    @Builder.Default
    private ApplicationSource applicationSource = ApplicationSource.OFFICIAL;

    @Column(name = "source_platform", length = 100)
    private String sourcePlatform; // 외부 지원 건의 플랫폼명 (잡코리아, 원티드 등) 자유 입력

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "result_updated_at")
    private LocalDateTime resultUpdatedAt;

    @Column(name = "interview_at")
    private LocalDateTime interviewAt;

    public void updateStatus(ApplicationStatus status, LocalDateTime interviewAt) {
        this.status = status;
        this.resultUpdatedAt = LocalDateTime.now();
        if (status == ApplicationStatus.INTERVIEW_SCHEDULED) {
            this.interviewAt = interviewAt;
        }
    }

    public String getEffectiveCompanyName() {
        return jobPosting != null ? jobPosting.getCompanyName() : companyName;
    }

    public String getEffectivePositionName() {
        return jobPosition != null ? jobPosition.getName() : null;
    }

    public boolean isExternal() {
        return applicationSource != ApplicationSource.OFFICIAL;
    }
}
