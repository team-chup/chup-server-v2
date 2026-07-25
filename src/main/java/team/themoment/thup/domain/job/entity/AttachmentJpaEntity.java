package team.themoment.thup.domain.job.entity;

import jakarta.persistence.*;
import lombok.*;
import team.themoment.thup.domain.job.entity.constant.AttachmentFileType;

@Entity
@Table(name = "tb_attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AttachmentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPostingJpaEntity jobPosting;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private AttachmentFileType fileType;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;
}