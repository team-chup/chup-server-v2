package team.themoment.thup.domain.transportsubsidy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.transportsubsidy.dto.MyTransportSubsidyResponse;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyApplicationJpaEntity;
import team.themoment.thup.domain.transportsubsidy.entity.TransportSubsidyEvidenceJpaEntity;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyApplicationRepository;
import team.themoment.thup.domain.transportsubsidy.repository.TransportSubsidyEvidenceRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryMyTransportSubsidyApplicationsService {

    private final TransportSubsidyApplicationRepository applicationRepository;
    private final TransportSubsidyEvidenceRepository evidenceRepository;

    public List<MyTransportSubsidyResponse> execute(OAuth2User user) {
        Long userId = ((Number) user.getAttribute("id")).longValue();
        List<TransportSubsidyApplicationJpaEntity> applications = applicationRepository.findAllByUser_IdOrderByAppliedAtDesc(userId);

        List<Long> applicationIds = applications.stream().map(TransportSubsidyApplicationJpaEntity::getId).toList();
        Map<Long, List<TransportSubsidyEvidenceJpaEntity>> evidencesByApplicationId = evidenceRepository
                .findAllByApplication_IdIn(applicationIds).stream()
                .collect(Collectors.groupingBy(evidence -> evidence.getApplication().getId()));

        return applications.stream()
                .map(application -> MyTransportSubsidyResponse.from(
                        application, evidencesByApplicationId.getOrDefault(application.getId(), List.of())
                ))
                .toList();
    }
}
