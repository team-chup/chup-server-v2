package team.themoment.thup.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<ApplicationJpaEntity, Long> {

    boolean existsByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);

    boolean existsByJobPosition_Id(Long jobPositionId);

    @Query("select a.jobPosting.id from ApplicationJpaEntity a where a.user.id = :userId")
    List<Long> findJobPostingIdsByUser_Id(Long userId);

    long countByUser_Id(Long userId);

    long countByUser_IdAndStatus(Long userId, ApplicationStatus status);

    long countByStatus(ApplicationStatus status);

    long countByJobPosting_Id(Long jobPostingId);

    @Query("select a.jobPosting.id, count(a) from ApplicationJpaEntity a where a.jobPosting.id in :jobPostingIds group by a.jobPosting.id")
    List<Object[]> countGroupedByJobPostingIdIn(List<Long> jobPostingIds);

    @Query("select a from ApplicationJpaEntity a join fetch a.jobPosting join fetch a.jobPosition where a.user.id = :userId")
    List<ApplicationJpaEntity> findAllWithJobByUser_Id(Long userId);

    @Query("select a from ApplicationJpaEntity a join fetch a.user join fetch a.jobPosting join fetch a.jobPosition")
    List<ApplicationJpaEntity> findAllWithDetails();

    @Query("select a from ApplicationJpaEntity a join fetch a.user join fetch a.jobPosting join fetch a.jobPosition where a.jobPosting.id = :jobPostingId")
    List<ApplicationJpaEntity> findAllWithDetailsByJobPosting_Id(Long jobPostingId);
}