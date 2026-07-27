package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.user.dto.PendingAdminResDto;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryPendingAdminsService {

    private final UserRepository userRepository;

    public List<PendingAdminResDto> execute() {
        return userRepository.findAllByRoleAndApprovedFalseOrderByCreatedAtAsc(Role.ADMIN).stream()
                .map(PendingAdminResDto::of)
                .toList();
    }
}
