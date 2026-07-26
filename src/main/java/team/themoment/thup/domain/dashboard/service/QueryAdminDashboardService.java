package team.themoment.thup.domain.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.repository.ApplicationRepository;
import team.themoment.thup.domain.dashboard.dto.response.AdminDashboardResDto;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAdminDashboardService {

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    public AdminDashboardResDto execute() {
        long openJobs = jobPostingRepository.countByStatus(JobPostingStatus.RECRUITING);
        long totalApplicants = applicationRepository.count();
        long pending = applicationRepository.countByStatus(ApplicationStatus.APPLIED);
        long passed = applicationRepository.countByStatus(ApplicationStatus.PASSED);

        return new AdminDashboardResDto(openJobs, totalApplicants, pending, passed);
    }
}