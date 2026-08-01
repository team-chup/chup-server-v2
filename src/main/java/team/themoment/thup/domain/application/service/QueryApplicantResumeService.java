package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.ApplicationResumeJpaEntity;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.application.repository.ApplicationResumeRepository;
import team.themoment.thup.domain.application.util.ApplicantResumeFileNameBuilder;
import team.themoment.thup.global.storage.FileDownload;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.ZipBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicantResumeService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationResumeRepository applicationResumeRepository;
    private final FileStorageService fileStorageService;

    public FileDownload execute(Long applicationId) {
        ApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("지원 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<ApplicationResumeJpaEntity> resumes = applicationResumeRepository.findAllByApplication_Id(applicationId);
        if (resumes.isEmpty()) {
            throw new ExpectedException("이력서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Map<String, Integer> usedNames = new HashMap<>();
        List<ZipBuilder.Entry> entries = resumes.stream()
                .map(resume -> new ZipBuilder.Entry(
                        ZipBuilder.uniqueName(resume.getResumeFileName(), usedNames),
                        resume.getResumeSnapshotUrl()
                ))
                .toList();

        byte[] zipBytes = ZipBuilder.build(entries, fileStorageService);
        return new FileDownload(new ByteArrayResource(zipBytes), ApplicantResumeFileNameBuilder.buildZipFileName(application));
    }
}
