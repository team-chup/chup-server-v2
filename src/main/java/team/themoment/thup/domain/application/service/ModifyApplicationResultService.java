package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.repository.ApplicationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyApplicationResultService {

    private final ApplicationRepository applicationRepository;

    public ApplicationJpaEntity execute(Long applicationId, ApplicationStatus status) {
        ApplicationJpaEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ExpectedException("지원 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        application.updateStatus(status);
        return application;
    }
}