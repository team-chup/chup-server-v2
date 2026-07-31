package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.dto.AdminApplicantResponse;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.repository.ApplicationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryApplicantsService {

    private final ApplicationRepository applicationRepository;

    public List<AdminApplicantResponse> execute(Long jobPostingId) {
        List<ApplicationJpaEntity> applications = jobPostingId == null
                ? applicationRepository.findAllWithDetails()
                : applicationRepository.findAllWithDetailsByJobPosting_Id(jobPostingId);
        return applications.stream()
                .map(AdminApplicantResponse::from)
                .toList();
    }
}