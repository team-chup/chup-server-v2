package team.themoment.thup.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;

import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPositionJpaEntity, Long> {

    List<JobPositionJpaEntity> findAllByJobPosting_Id(Long jobPostingId);
}