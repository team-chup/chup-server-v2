package team.themoment.thup.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPostingJpaEntity, Long> {

    List<JobPostingJpaEntity> findAllByStatus(JobPostingStatus status);

    List<JobPostingJpaEntity> findAllByStatusOrderByRecruitEndAsc(JobPostingStatus status);

    long countByStatus(JobPostingStatus status);
}