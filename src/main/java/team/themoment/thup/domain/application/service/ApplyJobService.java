package team.themoment.thup.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.application.dto.ApplicationResponse;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.ApplicationResumeJpaEntity;
import team.themoment.thup.domain.application.mail.ApplicationEmailTemplates;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.application.repository.ApplicationResumeRepository;
import team.themoment.thup.domain.job.entity.JobPositionJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.repository.JobPositionRepository;
import team.themoment.thup.domain.job.repository.JobPostingRepository;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.ResumeRepository;
import team.themoment.thup.domain.user.repository.UserRepository;
import team.themoment.thup.global.mail.MailService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyJobService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationResumeRepository applicationResumeRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobPositionRepository jobPositionRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final MailService mailService;

    public ApplicationResponse execute(OAuth2User user, Long jobId, Long jobPositionId, List<Long> resumeIds) {
        Long userId = ((Number) user.getAttribute("id")).longValue();

        if (resumeIds == null || resumeIds.isEmpty()) {
            throw new ExpectedException("이력서를 하나 이상 선택해야 합니다.", HttpStatus.BAD_REQUEST);
        }

        if (applicationRepository.existsByUser_IdAndJobPosting_Id(userId, jobId)) {
            throw new ExpectedException("이미 지원한 공고입니다.", HttpStatus.CONFLICT);
        }

        UserJpaEntity foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        JobPostingJpaEntity jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ExpectedException("공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        JobPositionJpaEntity jobPosition = jobPositionRepository.findById(jobPositionId)
                .orElseThrow(() -> new ExpectedException("포지션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<ResumeJpaEntity> resumes = resumeRepository.findAllByIdInAndUser_Id(resumeIds, userId);
        if (resumes.size() != resumeIds.size()) {
            throw new ExpectedException("선택한 이력서를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        ApplicationJpaEntity application = applicationRepository.save(
                ApplicationJpaEntity.builder()
                        .user(foundUser)
                        .jobPosting(jobPosting)
                        .jobPosition(jobPosition)
                        .build()
        );

        resumes.forEach(resume -> applicationResumeRepository.save(
                ApplicationResumeJpaEntity.builder()
                        .application(application)
                        .resumeSnapshotUrl(resume.getFileUrl())
                        .resumeFileName(resume.getFileName())
                        .build()
        ));

        mailService.send(
                foundUser.getEmail(),
                ApplicationEmailTemplates.appliedSubject(jobPosting.getCompanyName()),
                ApplicationEmailTemplates.appliedBody(jobPosting.getCompanyName(), jobPosition.getName())
        );

        return ApplicationResponse.from(application);
    }
}
