package team.themoment.thup.domain.job.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.user.entity.UserJpaEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_job_posting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobPostingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserJpaEntity createdBy;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    @Column(name = "recruit_start", nullable = false)
    private LocalDate recruitStart;

    @Column(name = "recruit_end", nullable = false)
    private LocalDate recruitEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private JobPostingStatus status = JobPostingStatus.RECRUITING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void update(String companyName, String description, EmploymentType employmentType, LocalDate recruitStart, LocalDate recruitEnd) {
        this.companyName = companyName;
        this.description = description;
        this.employmentType = employmentType;
        this.recruitStart = recruitStart;
        this.recruitEnd = recruitEnd;
    }

    public void updateStatus(JobPostingStatus status) {
        this.status = status;
    }
}