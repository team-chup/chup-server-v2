package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.repository.ResumeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeJpaEntity execute(OAuth2User user) {
        Long userId = ((Number) user.getAttribute("id")).longValue();
        return resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ExpectedException("등록된 이력서가 없습니다.", HttpStatus.NOT_FOUND));
    }
}
