package team.themoment.thup.domain.transportsubsidy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.transportsubsidy.dto.MyTransportSubsidyResponse;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyApplicationRepository;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyEvidenceRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.StoredFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterTransportSubsidyApplicationService {

    private static final int MAX_EVIDENCE_FILES = 5;
    private static final long MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");

    private final TransportSubsidyApplicationRepository applicationRepository;
    private final TransportSubsidyEvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public MyTransportSubsidyResponse execute(OAuth2User user, String companyName, LocalDateTime interviewAt,
                                               List<MultipartFile> files) {
        Long userId = ((Number) user.getAttribute("id")).longValue();

        if (companyName == null || companyName.isBlank()) {
            throw new ExpectedException("면접 회사명을 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (interviewAt == null) {
            throw new ExpectedException("면접 일시를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        validateFiles(files);

        UserJpaEntity foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (foundUser.getRole() != Role.STUDENT) {
            throw new ExpectedException("학생만 교통비 지원을 신청할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        TransportSubsidyApplicationJpaEntity application = applicationRepository.save(
                TransportSubsidyApplicationJpaEntity.builder()
                        .user(foundUser)
                        .companyName(companyName)
                        .interviewAt(interviewAt)
                        .build()
        );

        List<TransportSubsidyEvidenceJpaEntity> evidences = files.stream()
                .map(file -> {
                    StoredFile stored = fileStorageService.store(file, "transport-subsidies/" + userId);
                    return evidenceRepository.save(
                            TransportSubsidyEvidenceJpaEntity.builder()
                                    .application(application)
                                    .fileName(stored.originalFileName())
                                    .fileUrl(stored.storageKey())
                                    .fileSize((int) stored.size())
                                    .build()
                    );
                })
                .toList();

        return MyTransportSubsidyResponse.from(application, evidences);
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ExpectedException("증빙 서류를 하나 이상 첨부해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (files.size() > MAX_EVIDENCE_FILES) {
            throw new ExpectedException("증빙 서류는 최대 " + MAX_EVIDENCE_FILES + "개까지 첨부할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new ExpectedException("빈 파일은 첨부할 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                throw new ExpectedException("이미지(jpg/png) 또는 PDF 파일만 첨부할 수 있습니다.", HttpStatus.BAD_REQUEST);
            }
            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new ExpectedException("파일 용량이 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE);
            }
        }
    }
}
