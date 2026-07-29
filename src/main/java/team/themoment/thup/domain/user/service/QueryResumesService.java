package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.user.dto.ResumeResponse;
import team.themoment.thup.domain.user.repository.ResumeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryResumesService {

    private final ResumeRepository resumeRepository;

    public List<ResumeResponse> execute(OAuth2User user) {
        Long userId = ((Number) user.getAttribute("id")).longValue();
        return resumeRepository.findAllByUser_Id(userId).stream()
                .map(ResumeResponse::from)
                .toList();
    }
}
