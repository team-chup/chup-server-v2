package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.user.dto.UserSearchResDto;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryUsersService {

    private final UserRepository userRepository;

    public List<UserSearchResDto> execute(Role role, String keyword) {
        return userRepository.searchByRoleAndKeyword(role, keyword).stream()
                .map(UserSearchResDto::of)
                .toList();
    }
}
