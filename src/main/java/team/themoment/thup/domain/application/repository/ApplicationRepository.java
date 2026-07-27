package team.themoment.thup.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {

    List<ApplicationJpaEntity> findAllByUser_Id(Long userId);

    List<ApplicationJpaEntity> findAllByJobPosting_Id(Long jobPostingId);

    boolean existsByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);

    boolean existsByJobPosition_Id(Long jobPositionId);

    @Query("select a.jobPosting.id from ApplicationJpaEntity a where a.user.id = :userId")
    List<Long> findJobPostingIdsByUser_Id(Long userId);

    long countByUser_Id(Long userId);

    long countByUser_IdAndStatus(Long userId, ApplicationStatus status);

    long countByStatus(ApplicationStatus status);
}