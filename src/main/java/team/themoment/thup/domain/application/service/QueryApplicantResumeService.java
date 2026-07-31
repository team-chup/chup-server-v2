package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.application.util.ApplicantResumeFileNameBuilder;
import team.themoment.thup.global.storage.FileDownload;
import team.themoment.thup.global.storage.FileStorageService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicantResumeService {

    private final ApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    public FileDownload execute(Long applicationId) {
        ApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("지원 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return new FileDownload(
                fileStorageService.loadAsResource(application.getResumeSnapshotUrl()),
                ApplicantResumeFileNameBuilder.build(application)
        );
    }
}
