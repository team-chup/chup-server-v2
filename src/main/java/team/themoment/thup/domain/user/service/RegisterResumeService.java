package team.themoment.thup.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.user.dto.ResumeResponse;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.ResumeRepository;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.StoredFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterResumeService {

    private static final int MAX_RESUMES = 3;
    private static final long MAX_RESUME_SIZE_BYTES = 10L * 1024 * 1024;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public List<ResumeResponse> execute(OAuth2User user, List<MultipartFile> files) {
        Long userId = ((Number) user.getAttribute("id")).longValue();

        if (files == null || files.isEmpty()) {
            throw new ExpectedException("이력서 파일이 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        files.forEach(this::validate);

        // 동시 업로드 요청이 카운트 체크를 동시에 통과해 3개 제한을 넘기지 않도록,
        // 유저 row에 락을 건 뒤 그 안에서 카운트를 확인한다.
        UserJpaEntity foundUser = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        long existingCount = resumeRepository.countByUser_Id(userId);
        if (existingCount + files.size() > MAX_RESUMES) {
            throw new ExpectedException("이력서는 최대 " + MAX_RESUMES + "개까지 등록할 수 있습니다.", HttpStatus.CONFLICT);
        }

        return files.stream()
                .map(file -> {
                    StoredFile stored = fileStorageService.store(file, "resumes/" + userId);
                    ResumeJpaEntity saved = resumeRepository.save(
                            ResumeJpaEntity.builder()
                                    .user(foundUser)
                                    .fileName(stored.originalFileName())
                                    .fileUrl(stored.storageKey())
                                    .fileSize((int) stored.size())
                                    .build()
                    );
                    return ResumeResponse.from(saved);
                })
                .toList();
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
