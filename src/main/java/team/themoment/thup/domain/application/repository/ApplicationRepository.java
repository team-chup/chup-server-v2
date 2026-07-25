package team.themoment.thup.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {

    List<ApplicationJpaEntity> findAllByUser_Id(Long userId);

    List<ApplicationJpaEntity> findAllByJobPosting_Id(Long jobPostingId);

    boolean existsByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);
}