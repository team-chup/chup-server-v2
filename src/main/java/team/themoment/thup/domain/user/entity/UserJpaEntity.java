package team.themoment.thup.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import team.themoment.thup.domain.user.entity.constant.Role;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "student_id", length = 20)
    private String studentId;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "approved", nullable = false)
    @Builder.Default
    private boolean approved = false;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void approve() {
        this.approved = true;
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
        this.approved = false;
    }

    public void switchRole(Role role) {
        this.role = role;
        this.approved = true;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateGrade(Integer grade) {
        this.grade = grade;
    }
}