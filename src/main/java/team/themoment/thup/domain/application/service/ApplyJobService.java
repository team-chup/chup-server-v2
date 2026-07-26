package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.mail.ApplicationEmailTemplates;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.ResumeRepository;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.mail.MailService;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyJobService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final MailService mailService;

    public ApplicationJpaEntity execute(OAuth2User user, Long jobId, Long jobPositionId) {
        Long userId = ((Number) user.getAttribute("id")).longValue();

        if (applicationRepository.existsByUser_IdAndJobPosting_Id(userId, jobId)) {
            throw new ExpectedException("이미 지원한 공고입니다.", HttpStatus.CONFLICT);
        }

        UserJpaEntity foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        JobPostingJpaEntity jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        JobPositionJpaEntity jobPosition = jobPositionRepository.findById(jobPositionId)
                .orElseThrow(() -> new ExpectedException("포지션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        ResumeJpaEntity resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ExpectedException("등록된 이력서가 없습니다.", HttpStatus.BAD_REQUEST));

        ApplicationJpaEntity application = applicationRepository.save(
                ApplicationJpaEntity.builder()
                        .user(foundUser)
                        .jobPosting(jobPosting)
                        .jobPosition(jobPosition)
                        .resumeSnapshotUrl(resume.getFileUrl())
                        .build()
        );

        mailService.send(
                foundUser.getEmail(),
                ApplicationEmailTemplates.appliedSubject(jobPosting.getCompanyName()),
                ApplicationEmailTemplates.appliedBody(jobPosting.getCompanyName(), jobPosition.getName())
        );

        return application;
    }
}
