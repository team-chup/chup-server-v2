package team.themoment.thup.domain.transportsubsidy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_transport_subsidy_evidence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TransportSubsidyEvidenceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private TransportSubsidyApplicationJpaEntity application;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;
}
