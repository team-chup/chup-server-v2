package team.themoment.thup.domain.transportsubsidy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;

import java.util.List;

public interface TransportSubsidyApplicationRepository extends JpaRepository<TransportSubsidyApplicationJpaEntity, Long> {

    long countByUser_IdAndStatus(Long userId, TransportSubsidyStatus status);

    List<TransportSubsidyApplicationJpaEntity> findAllByUser_IdOrderByAppliedAtDesc(Long userId);

    @Query("select a from TransportSubsidyApplicationJpaEntity a join fetch a.user order by a.appliedAt desc")
    List<TransportSubsidyApplicationJpaEntity> findAllWithUser();

    @Query("select a from TransportSubsidyApplicationJpaEntity a join fetch a.user where a.user.id = :userId order by a.appliedAt desc")
    List<TransportSubsidyApplicationJpaEntity> findAllWithUserByUser_Id(Long userId);

    @Query("select a.user.id, count(a) from TransportSubsidyApplicationJpaEntity a where a.status = :status group by a.user.id")
    List<Object[]> countGroupedByUserIdAndStatus(TransportSubsidyStatus status);

    @Query("select a.user.id, count(a) from TransportSubsidyApplicationJpaEntity a group by a.user.id")
    List<Object[]> countGroupedByUserId();
}
