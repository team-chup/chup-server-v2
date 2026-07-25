package team.themoment.thup.domain.job.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;
import team.themoment.thup.domain.job.entity.JobPostingJpaEntity;
import team.themoment.thup.domain.job.entity.constant.EmploymentType;
import team.themoment.thup.domain.job.entity.constant.JobPostingStatus;
import team.themoment.thup.domain.job.service.CreateJobPostingService;
import team.themoment.thup.domain.job.service.ModifyJobPostingService;
import team.themoment.thup.domain.job.service.ModifyJobPostingStatusService;
import team.themoment.thup.domain.job.service.QueryAdminJobPostingsService;
import team.themoment.thup.domain.job.service.QueryAttachmentService;
import team.themoment.thup.domain.job.service.QueryJobPostingService;
import team.themoment.thup.domain.job.service.QueryJobPostingsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final QueryJobPostingsService queryJobPostingsService;
    private final QueryJobPostingService queryJobPostingService;
    private final QueryAttachmentService queryAttachmentService;
    private final QueryAdminJobPostingsService queryAdminJobPostingsService;
    private final CreateJobPostingService createJobPostingService;
    private final ModifyJobPostingService modifyJobPostingService;
    private final ModifyJobPostingStatusService modifyJobPostingStatusService;

    @GetMapping("/api/jobs")
    public List<JobPostingJpaEntity> queryJobPostings() {
        return queryJobPostingsService.execute();
    }

    @GetMapping("/api/jobs/{jobId}")
    public JobPostingJpaEntity queryJobPosting(@PathVariable Long jobId) {
        return queryJobPostingService.execute(jobId);
    }

    @GetMapping("/api/jobs/{jobId}/attachments/{fileId}")
    public AttachmentJpaEntity downloadAttachment(@PathVariable Long jobId, @PathVariable Long fileId) {
        return queryAttachmentService.execute(jobId, fileId);
    }

    @GetMapping("/api/admin/jobs")
    public List<JobPostingJpaEntity> queryAdminJobPostings() {
        return queryAdminJobPostingsService.execute();
    }

    @PostMapping("/api/admin/jobs")
    public JobPostingJpaEntity createJobPosting(@AuthenticationPrincipal OAuth2User admin, @RequestBody JobPostingRequest request) {
        return createJobPostingService.execute(admin, request.companyName(), request.description(),
                request.employmentType(), request.recruitStart(), request.recruitEnd(), request.positionNames());
    }

    @PatchMapping("/api/admin/jobs/{jobId}")
    public JobPostingJpaEntity modifyJobPosting(@PathVariable Long jobId, @RequestBody JobPostingRequest request) {
        return modifyJobPostingService.execute(jobId, request.companyName(), request.description(),
                request.employmentType(), request.recruitStart(), request.recruitEnd());
    }

    @PatchMapping("/api/admin/jobs/{jobId}/status")
    public JobPostingJpaEntity modifyJobPostingStatus(@PathVariable Long jobId, @RequestBody JobPostingStatusRequest request) {
        return modifyJobPostingStatusService.execute(jobId, request.status());
    }

    private record JobPostingRequest(String companyName, String description, EmploymentType employmentType,
                                      LocalDate recruitStart, LocalDate recruitEnd, List<String> positionNames) {}

    private record JobPostingStatusRequest(JobPostingStatus status) {}
}