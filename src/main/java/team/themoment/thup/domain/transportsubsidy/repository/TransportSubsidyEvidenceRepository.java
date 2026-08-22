package team.themoment.thup.domain.transportsubsidy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;

import java.util.List;

public interface TransportSubsidyEvidenceRepository extends JpaRepository<TransportSubsidyEvidenceJpaEntity, Long> {

    List<TransportSubsidyEvidenceJpaEntity> findAllByApplication_Id(Long applicationId);

    List<TransportSubsidyEvidenceJpaEntity> findAllByApplication_IdIn(List<Long> applicationIds);
}
