package team.themoment.thup.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<ResumeJpaEntity, Long> {

    List<ResumeJpaEntity> findAllByUser_Id(Long userId);

    Optional<ResumeJpaEntity> findByIdAndUser_Id(Long id, Long userId);

    List<ResumeJpaEntity> findAllByIdInAndUser_Id(List<Long> ids, Long userId);

    long countByUser_Id(Long userId);
}