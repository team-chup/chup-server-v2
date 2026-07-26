package team.themoment.thup.domain.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.dashboard.dto.response.RecommendedJobResDto;
import team.themoment.thup.domain.dashboard.dto.response.StudentDashboardResDto;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryStudentDashboardService {

    private static final int RECOMMENDED_JOB_LIMIT = 3;

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    public StudentDashboardResDto execute(OAuth2User user) {
        Long userId = ((Number) user.getAttribute("id")).longValue();

        long openJobs = jobPostingRepository.countByStatus(JobPostingStatus.RECRUITING);
        long myApplications = applicationRepository.countByUser_Id(userId);
        long passed = applicationRepository.countByUser_IdAndStatus(userId, ApplicationStatus.PASSED);

        Set<Long> appliedJobPostingIds = new HashSet<>(applicationRepository.findJobPostingIdsByUser_Id(userId));
        List<RecommendedJobResDto> recommendedJobs = jobPostingRepository
                .findAllByStatusOrderByRecruitEndAsc(JobPostingStatus.RECRUITING).stream()
                .filter(job -> !appliedJobPostingIds.contains(job.getId()))
                .limit(RECOMMENDED_JOB_LIMIT)
                .map(job -> new RecommendedJobResDto(
                        job.getId(),
                        job.getCompanyName(),
                        ChronoUnit.DAYS.between(LocalDate.now(), job.getRecruitEnd())
                ))
                .toList();

        return new StudentDashboardResDto(openJobs, myApplications, passed, recommendedJobs);
    }
}