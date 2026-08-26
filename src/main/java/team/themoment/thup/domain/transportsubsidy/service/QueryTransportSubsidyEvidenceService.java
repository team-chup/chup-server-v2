package team.themoment.thup.domain.transportsubsidy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyApplicationRepository;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyEvidenceRepository;
import team.themoment.thup.global.storage.FileDownload;
import team.themoment.thup.global.storage.FileStorageService;
import team.themoment.thup.global.storage.ZipBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryTransportSubsidyEvidenceService {

    private final TransportSubsidyApplicationRepository applicationRepository;
    private final TransportSubsidyEvidenceRepository evidenceRepository;
    private final FileStorageService fileStorageService;

    public FileDownload execute(Long applicationId) {
        TransportSubsidyApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<TransportSubsidyEvidenceJpaEntity> evidences = evidenceRepository.findAllByApplication_Id(applicationId);
        if (evidences.isEmpty()) {
            throw new ExpectedException("증빙 서류를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Map<String, Integer> usedNames = new HashMap<>();
        List<ZipBuilder.Entry> entries = evidences.stream()
                .map(evidence -> new ZipBuilder.Entry(
                        ZipBuilder.uniqueName(evidence.getFileName(), usedNames),
                        evidence.getFileUrl()
                ))
                .toList();

        byte[] zipBytes = ZipBuilder.build(entries, fileStorageService);
        String zipFileName = "%s_%s_증빙서류.zip".formatted(
                sanitize(application.getUser().getName()), sanitize(application.getCompanyName())
        );

        return new FileDownload(new ByteArrayResource(zipBytes), zipFileName);
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replaceAll("[\\\\/:*?\"<>|]", "");
    }
}
