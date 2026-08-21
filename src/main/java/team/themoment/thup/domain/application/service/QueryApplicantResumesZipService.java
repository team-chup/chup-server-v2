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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicantResumesZipService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationResumeRepository applicationResumeRepository;
    private final FileStorageService fileStorageService;

    public FileDownload execute(Long companyId) {
        List<ApplicationJpaEntity> applications = companyId == null
                ? applicationRepository.findAllWithDetails()
                : applicationRepository.findAllWithDetailsByJobPosting_Id(companyId);

        if (applications.isEmpty()) {
            throw new ExpectedException("지원자가 없습니다.", HttpStatus.NOT_FOUND);
        }

        byte[] zipBytes = ZipBuilder.build(buildEntries(applications), fileStorageService);
        String zipFileName = companyId == null
                ? "전체.zip"
                : applications.get(0).getJobPosting().getCompanyName() + ".zip";

        return new FileDownload(new ByteArrayResource(zipBytes), zipFileName);
    }

    private List<ZipBuilder.Entry> buildEntries(List<ApplicationJpaEntity> applications) {
        // 외부 지원 건은 이력서 업로드 경로가 없고 jobPosition도 없어 파일명 라벨을 만들 수 없으므로 제외한다.
        List<ApplicationJpaEntity> officialApplications = applications.stream()
                .filter(application -> !application.isExternal())
                .toList();

        List<Long> applicationIds = officialApplications.stream().map(ApplicationJpaEntity::getId).toList();
        Map<Long, List<ApplicationResumeJpaEntity>> resumesByApplicationId = applicationResumeRepository
                .findAllByApplication_IdIn(applicationIds).stream()
                .collect(Collectors.groupingBy(resume -> resume.getApplication().getId()));

        Map<String, Integer> usedFolderNames = new HashMap<>();
        List<ZipBuilder.Entry> entries = new ArrayList<>();

        for (ApplicationJpaEntity application : officialApplications) {
            String folderName = ZipBuilder.uniqueName(ApplicantResumeFileNameBuilder.buildLabel(application), usedFolderNames);
            List<ApplicationResumeJpaEntity> resumes = resumesByApplicationId.getOrDefault(application.getId(), List.of());

            Map<String, Integer> usedFileNames = new HashMap<>();
            for (ApplicationResumeJpaEntity resume : resumes) {
                String fileName = ZipBuilder.uniqueName(resume.getResumeFileName(), usedFileNames);
                entries.add(new ZipBuilder.Entry(folderName + "/" + fileName, resume.getResumeSnapshotUrl()));
            }
        }

        return entries;
    }
}