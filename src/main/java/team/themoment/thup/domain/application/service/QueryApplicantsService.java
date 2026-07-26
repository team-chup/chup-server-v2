package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.repository.ApplicationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicantsService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationJpaEntity> execute(Long companyId) {
        if (companyId == null) {
            return applicationRepository.findAll();
        }
        return applicationRepository.findAllByJobPosting_Id(companyId);
    }
}