package team.themoment.thup.domain.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.service.ApplyJobService;
import team.themoment.thup.domain.application.service.ModifyApplicationResultService;
import team.themoment.thup.domain.application.service.QueryApplicantResumeService;
import team.themoment.thup.domain.application.service.QueryApplicantResumesZipService;
import team.themoment.thup.domain.application.service.QueryApplicantsService;
import team.themoment.thup.domain.application.service.QueryMyApplicationsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplyJobService applyJobService;
    private final QueryMyApplicationsService queryMyApplicationsService;
    private final QueryApplicantsService queryApplicantsService;
    private final ModifyApplicationResultService modifyApplicationResultService;
    private final QueryApplicantResumeService queryApplicantResumeService;
    private final QueryApplicantResumesZipService queryApplicantResumesZipService;

    @PostMapping("/api/jobs/{jobId}/applications")
    public ApplicationJpaEntity apply(@AuthenticationPrincipal OAuth2User user, @PathVariable Long jobId,
                                       @RequestBody ApplyRequest request) {
        return applyJobService.execute(user, jobId, request.jobPositionId());
    }

    @GetMapping("/api/applications")
    public List<ApplicationJpaEntity> queryMyApplications(@AuthenticationPrincipal OAuth2User user) {
        return queryMyApplicationsService.execute(user);
    }

    @GetMapping("/api/admin/applicants")
    public List<ApplicationJpaEntity> queryApplicants() {
        return queryApplicantsService.execute();
    }

    @GetMapping("/api/admin/applicants/zip")
    public List<ApplicationJpaEntity> downloadApplicantsZip() {
        return queryApplicantResumesZipService.execute();
    }

    @GetMapping("/api/admin/applicants/{applicationId}/resume")
    public ApplicationJpaEntity downloadApplicantResume(@PathVariable Long applicationId) {
        return queryApplicantResumeService.execute(applicationId);
    }

    @PatchMapping("/api/admin/applicants/{applicationId}/result")
    public ApplicationJpaEntity modifyApplicationResult(@PathVariable Long applicationId, @RequestBody ResultRequest request) {
        return modifyApplicationResultService.execute(applicationId, request.status());
    }

    private record ApplyRequest(Long jobPositionId) {}

    private record ResultRequest(ApplicationStatus status) {}
}