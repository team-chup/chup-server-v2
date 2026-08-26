package team.themoment.thup.domain.user.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    List<UserJpaEntity> findAllByRoleAndApprovedFalseOrderByCreatedAtAsc(Role role);

    List<UserJpaEntity> findAllByRoleAndGrade(Role role, Integer grade);

    @Query("select u from UserJpaEntity u " +
            "where (:role is null or u.role = :role) " +
            "and (:keyword is null or :keyword = '' " +
            "     or lower(u.name) like lower(concat('%', :keyword, '%')) " +
            "     or u.studentId like concat('%', :keyword, '%')) " +
            "order by u.name asc")
    List<UserJpaEntity> searchByRoleAndKeyword(@Param("role") Role role, @Param("keyword") String keyword);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserJpaEntity u where u.id = :id")
    Optional<UserJpaEntity> findByIdForUpdate(@Param("id") Long id);
}