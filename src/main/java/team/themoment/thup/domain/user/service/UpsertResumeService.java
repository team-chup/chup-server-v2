package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.ResumeRepository;
import team.themoment.thup.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UpsertResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeJpaEntity execute(OAuth2User user, String fileName, String fileUrl, Integer fileSize) {
        Long userId = ((Number) user.getAttribute("id")).longValue();

        return resumeRepository.findByUser_Id(userId)
                .map(resume -> {
                    resume.update(fileName, fileUrl, fileSize);
                    return resume;
                })
                .orElseGet(() -> {
                    UserJpaEntity foundUser = userRepository.findById(userId)
                            .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
                    return resumeRepository.save(
                            ResumeJpaEntity.builder()
                                    .user(foundUser)
                                    .fileName(fileName)
                                    .fileUrl(fileUrl)
                                    .fileSize(fileSize)
                                    .build()
                    );
                });
    }
}