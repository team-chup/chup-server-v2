package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.application.util.ApplicantResumeFileNameBuilder;
import team.themoment.thup.global.storage.FileDownload;
import team.themoment.thup.global.storage.FileStorageService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicantResumesZipService {

    private final ApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    public FileDownload execute(Long companyId) {
        List<ApplicationJpaEntity> applications = companyId == null
                ? applicationRepository.findAll()
                : applicationRepository.findAllByJobPosting_Id(companyId);

        if (applications.isEmpty()) {
            throw new ExpectedException("지원자가 없습니다.", HttpStatus.NOT_FOUND);
        }

        byte[] zipBytes = buildZip(applications);
        String zipFileName = companyId == null
                ? "전체.zip"
                : applications.get(0).getJobPosting().getCompanyName() + ".zip";

        return new FileDownload(new ByteArrayResource(zipBytes), zipFileName);
    }

    private byte[] buildZip(List<ApplicationJpaEntity> applications) {
        Map<String, Integer> usedEntryNames = new HashMap<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(buffer)) {
            for (ApplicationJpaEntity application : applications) {
                String entryName = uniqueEntryName(ApplicantResumeFileNameBuilder.build(application), usedEntryNames);
                zipOutputStream.putNextEntry(new ZipEntry(entryName));
                try (InputStream inputStream = fileStorageService.loadAsResource(application.getResumeSnapshotUrl()).getInputStream()) {
                    inputStream.transferTo(zipOutputStream);
                }
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new ExpectedException("ZIP 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return buffer.toByteArray();
    }

    private String uniqueEntryName(String fileName, Map<String, Integer> usedEntryNames) {
        int count = usedEntryNames.merge(fileName, 1, Integer::sum);
        if (count == 1) {
            return fileName;
        }
        int dotIndex = fileName.lastIndexOf('.');
        String base = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
        String extension = dotIndex == -1 ? "" : fileName.substring(dotIndex);
        return "%s(%d)%s".formatted(base, count - 1, extension);
    }
}