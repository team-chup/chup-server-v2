package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.dto.ApplicationResponse;
import team.themoment.thup.domain.application.repository.ApplicationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryMyApplicationsService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationResponse> execute(OAuth2User user) {
        Long userId = ((Number) user.getAttribute("id")).longValue();
        return applicationRepository.findAllWithJobByUser_Id(userId).stream()
                .map(ApplicationResponse::from)
                .toList();
    }
}