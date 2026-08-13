package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.job.repository.JobPostingRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CloseExpiredJobPostingsService {

    private final JobPostingRepository jobPostingRepository;

    /**
     * 모집 마감일이 지난 모집중 공고를 마감 처리하고, 마감된 공고의 회사명 목록을 반환한다.
     * recruitEnd 당일까지는 모집 기간에 포함되므로 recruitEnd가 today보다 이전인 공고만 대상으로 한다.
     */
    public List<String> execute(LocalDate today) {
        List<JobPostingJpaEntity> expired = jobPostingRepository
                .findAllByStatusAndRecruitEndBefore(JobPostingStatus.RECRUITING, today);
        expired.forEach(jobPosting -> jobPosting.updateStatus(JobPostingStatus.CLOSED));
        return expired.stream().map(JobPostingJpaEntity::getCompanyName).toList();
    }
}
