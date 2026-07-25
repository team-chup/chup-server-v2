package team.themoment.thup.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<AttachmentJpaEntity, Long> {

    List<AttachmentJpaEntity> findAllByJobPosting_Id(Long jobPostingId);

    Optional<AttachmentJpaEntity> findByIdAndJobPosting_Id(Long id, Long jobPostingId);
}