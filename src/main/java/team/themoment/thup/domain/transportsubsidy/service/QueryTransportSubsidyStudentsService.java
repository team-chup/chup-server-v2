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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryTransportSubsidyStudentsService {

    private static final int TARGET_GRADE = 3;

    private final UserRepository userRepository;
    private final TransportSubsidyApplicationRepository applicationRepository;

    public List<TransportSubsidyStudentSummaryResponse> execute() {
        List<UserJpaEntity> students = userRepository.findAllByRoleAndGrade(Role.STUDENT, TARGET_GRADE);

        Map<Long, Long> approvedCountByUserId = toCountMap(
                applicationRepository.countGroupedByUserIdAndStatus(TransportSubsidyStatus.APPROVED)
        );
        Map<Long, Long> totalCountByUserId = toCountMap(applicationRepository.countGroupedByUserId());

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
