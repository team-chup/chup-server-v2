package team.themoment.thup.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    List<UserJpaEntity> findAllByRoleAndApprovedFalseOrderByCreatedAtAsc(Role role);
}