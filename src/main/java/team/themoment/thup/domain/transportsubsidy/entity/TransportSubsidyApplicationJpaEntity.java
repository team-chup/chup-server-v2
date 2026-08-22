package team.themoment.thup.domain.transportsubsidy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;
import team.themoment.thup.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transport_subsidy_application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TransportSubsidyApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "interview_at", nullable = false)
    private LocalDateTime interviewAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransportSubsidyStatus status = TransportSubsidyStatus.PENDING;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "result_updated_at")
    private LocalDateTime resultUpdatedAt;

    public void updateStatus(TransportSubsidyStatus status) {
        this.status = status;
        this.resultUpdatedAt = LocalDateTime.now();
    }
}
