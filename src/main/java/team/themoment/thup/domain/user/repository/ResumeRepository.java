package team.themoment.thup.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<ResumeJpaEntity, Long> {

    Optional<ResumeJpaEntity> findByUser_Id(Long userId);
}