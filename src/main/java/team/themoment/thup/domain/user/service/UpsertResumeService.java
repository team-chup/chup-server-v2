package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.ResumeRepository;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.StoredFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UpsertResumeService {

    private static final long MAX_RESUME_SIZE_BYTES = 10L * 1024 * 1024;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ResumeJpaEntity execute(OAuth2User user, MultipartFile file) {
        Long userId = ((Number) user.getAttribute("id")).longValue();
        validate(file);

        StoredFile stored = fileStorageService.store(file, "resumes/" + userId);

        return resumeRepository.findByUser_Id(userId)
                .map(resume -> {
                    resume.update(stored.originalFileName(), stored.storageKey(), (int) stored.size());
                    return resume;
                })
                .orElseGet(() -> {
                    UserJpaEntity foundUser = userRepository.findById(userId)
                            .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
                    return resumeRepository.save(
                            ResumeJpaEntity.builder()
                                    .user(foundUser)
                                    .fileName(stored.originalFileName())
                                    .fileUrl(stored.storageKey())
                                    .fileSize((int) stored.size())
                                    .build()
                    );
                });
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExpectedException("이력서 파일이 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        boolean isPdfContentType = "application/pdf".equals(file.getContentType());
        boolean isPdfExtension = file.getOriginalFilename() != null
                && file.getOriginalFilename().toLowerCase().endsWith(".pdf");
        if (!isPdfContentType && !isPdfExtension) {
            throw new ExpectedException("PDF 파일만 업로드할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_RESUME_SIZE_BYTES) {
            throw new ExpectedException("파일 용량이 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }
}