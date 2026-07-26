package team.themoment.thup.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Application", description = "지원 API")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplyJobService applyJobService;
    private final QueryMyApplicationsService queryMyApplicationsService;
    private final QueryApplicantsService queryApplicantsService;
    private final ModifyApplicationResultService modifyApplicationResultService;
    private final QueryApplicantResumeService queryApplicantResumeService;
    private final QueryApplicantResumesZipService queryApplicantResumesZipService;

    @Operation(summary = "지원하기", description = "채용 공고의 특정 포지션에 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 성공"),
            @ApiResponse(responseCode = "400", description = "등록된 이력서가 없음"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자, 공고 또는 포지션을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 지원한 공고")
    })
    @PostMapping("/api/jobs/{jobId}/applications")
    public ApplicationJpaEntity apply(@AuthenticationPrincipal OAuth2User user, @PathVariable Long jobId,
                                       @RequestBody ApplyRequest request) {
        return applyJobService.execute(user, jobId, request.jobPositionId());
    }

    @Operation(summary = "내 지원 현황 조회", description = "현재 로그인한 사용자의 지원 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/api/applications")
    public List<ApplicationJpaEntity> queryMyApplications(@AuthenticationPrincipal OAuth2User user) {
        return queryMyApplicationsService.execute(user);
    }

    @Operation(summary = "지원자 목록 조회", description = "전체 또는 특정 공고(jobPostingId=공고 ID)의 지원자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/applicants")
    public List<ApplicationJpaEntity> queryApplicants(@RequestParam(required = false) Long jobPostingId) {
        return queryApplicantsService.execute(jobPostingId);
    }

    @Operation(summary = "지원자 이력서 ZIP 다운로드", description = "전체 지원자의 이력서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/applicants/zip")
    public List<ApplicationJpaEntity> downloadApplicantsZip() {
        return queryApplicantResumesZipService.execute();
    }

    @Operation(summary = "지원자 이력서 다운로드", description = "특정 지원 건의 이력서 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "지원 내역을 찾을 수 없음")
    })
    @GetMapping("/api/admin/applicants/{applicationId}/resume")
    public ApplicationJpaEntity downloadApplicantResume(@PathVariable Long applicationId) {
        return queryApplicantResumeService.execute(applicationId);
    }

    @Operation(summary = "지원 결과 처리", description = "지원 건의 서류 합격/불합격 결과를 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "지원 내역을 찾을 수 없음")
    })
    @PatchMapping("/api/admin/applicants/{applicationId}/result")
    public ApplicationJpaEntity modifyApplicationResult(@PathVariable Long applicationId, @RequestBody ResultRequest request) {
        return modifyApplicationResultService.execute(applicationId, request.status());
    }

    private record ApplyRequest(Long jobPositionId) {}

    private record ResultRequest(ApplicationStatus status) {}
}