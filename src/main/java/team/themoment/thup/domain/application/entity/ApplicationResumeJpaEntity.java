package team.themoment.thup.domain.application.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_application_resume")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicationResumeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationJpaEntity application;

    @Column(name = "resume_snapshot_url", nullable = false, length = 1000)
    private String resumeSnapshotUrl;

    @Column(name = "resume_file_name", nullable = false, length = 255)
    private String resumeFileName;
}
