package team.themoment.thup.domain.job.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_job_position")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JobPositionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPostingJpaEntity jobPosting;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}