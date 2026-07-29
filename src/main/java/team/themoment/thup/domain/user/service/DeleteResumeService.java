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
@Transactional
public class DeleteResumeService {

    private final ResumeRepository resumeRepository;

    public void execute(OAuth2User user, Long resumeId) {
        Long userId = ((Number) user.getAttribute("id")).longValue();
        ResumeJpaEntity resume = resumeRepository.findByIdAndUser_Id(resumeId, userId)
                .orElseThrow(() -> new ExpectedException("등록된 이력서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        resumeRepository.delete(resume);
    }
}
