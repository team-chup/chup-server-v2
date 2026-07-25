package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateJobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final UserRepository userRepository;

    public JobPostingJpaEntity execute(OAuth2User admin, String companyName, String description,
                                        EmploymentType employmentType, LocalDate recruitStart, LocalDate recruitEnd,
                                        List<String> positionNames) {
        Long adminId = ((Number) admin.getAttribute("id")).longValue();
        UserJpaEntity createdBy = userRepository.findById(adminId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        JobPostingJpaEntity saved = jobPostingRepository.save(
                JobPostingJpaEntity.builder()
                        .createdBy(createdBy)
                        .companyName(companyName)
                        .description(description)
                        .employmentType(employmentType)
                        .recruitStart(recruitStart)
                        .recruitEnd(recruitEnd)
                        .build()
        );

        positionNames.forEach(name -> jobPositionRepository.save(
                JobPositionJpaEntity.builder()
                        .jobPosting(saved)
                        .name(name)
                        .build()
        ));

        return saved;
    }
}