package team.themoment.thup.domain.transportsubsidy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.transportsubsidy.dto.TransportSubsidyStudentSummaryResponse;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyApplicationRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryTransportSubsidyStudentsService {

    private static final int TARGET_GRADE = 3;

    private final UserRepository userRepository;
    private final TransportSubsidyApplicationRepository applicationRepository;

    public List<TransportSubsidyStudentSummaryResponse> execute() {
        List<UserJpaEntity> gradeThreeStudents = userRepository.findAllByRoleAndGrade(Role.STUDENT, TARGET_GRADE);

        Map<Long, Long> approvedCountByUserId = toCountMap(
                applicationRepository.countGroupedByUserIdAndStatus(TransportSubsidyStatus.APPROVED)
        );
        Map<Long, Long> totalCountByUserId = toCountMap(applicationRepository.countGroupedByUserId());

        // 신청 이후 역할이 바뀐(예: 관리자로 승격된) 유저도 신청 이력이 있으면 계속 노출되어야 하므로,
        // 3학년 재학생 목록에 신청 이력이 있는 유저를 role/grade와 무관하게 합쳐서 보여준다.
        Set<Long> gradeThreeStudentIds = gradeThreeStudents.stream().map(UserJpaEntity::getId).collect(Collectors.toSet());
        List<Long> applicantsOutsideGradeThree = totalCountByUserId.keySet().stream()
                .filter(userId -> !gradeThreeStudentIds.contains(userId))
                .toList();
        List<UserJpaEntity> otherApplicants = applicantsOutsideGradeThree.isEmpty()
                ? List.of()
                : userRepository.findAllById(applicantsOutsideGradeThree);

        List<UserJpaEntity> students = Stream.concat(gradeThreeStudents.stream(), otherApplicants.stream()).toList();

        return students.stream()
                .map(student -> new TransportSubsidyStudentSummaryResponse(
                        student.getId(),
                        student.getName(),
                        student.getStudentId(),
                        approvedCountByUserId.getOrDefault(student.getId(), 0L),
                        totalCountByUserId.getOrDefault(student.getId(), 0L)
                ))
                .toList();
    }

    private Map<Long, Long> toCountMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }
}
