package team.themoment.thup.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.notice.entity.NoticeJpaEntity;

import java.util.List;

public interface NoticeRepository extends JpaRepository<NoticeJpaEntity, Long> {

    List<NoticeJpaEntity> findAllByOrderByCreatedAtDescIdDesc();
}