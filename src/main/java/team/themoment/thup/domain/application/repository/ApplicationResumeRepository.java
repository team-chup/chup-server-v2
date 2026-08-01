package team.themoment.thup.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.application.entity.ApplicationResumeJpaEntity;

import java.util.List;

public interface ApplicationResumeRepository extends JpaRepository<ApplicationResumeJpaEntity, Long> {

    List<ApplicationResumeJpaEntity> findAllByApplication_Id(Long applicationId);

    List<ApplicationResumeJpaEntity> findAllByApplication_IdIn(List<Long> applicationIds);
}
