package team.themoment.thup.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import team.themoment.thup.domain.application.dto.AdminApplicantResponse;
import team.themoment.thup.domain.application.dto.ApplicationResponse;
import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;
import team.themoment.thup.domain.application.service.ApplyJobService;
import team.themoment.thup.domain.application.service.ModifyApplicationResultService;
import team.themoment.thup.domain.application.service.QueryApplicantResumeService;
import team.themoment.thup.domain.application.service.QueryApplicantResumesZipService;
import team.themoment.thup.domain.application.service.QueryApplicantsService;
import team.themoment.thup.domain.application.service.QueryMyApplicationsService;
import team.themoment.thup.domain.application.service.RegisterManualApplicantService;
import team.themoment.thup.domain.application.service.RegisterMyExternalApplicationService;
import team.themoment.thup.global.storage.FileDownloadResponses;

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
    private final RegisterManualApplicantService registerManualApplicantService;
    private final RegisterMyExternalApplicationService registerMyExternalApplicationService;

    @Operation(summary = "지원하기", description = "채용 공고의 특정 포지션에, 등록된 이력서 중 하나 이상(resumeIds)을 선택해 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 성공"),
            @ApiResponse(responseCode = "400", description = "이력서를 선택하지 않았거나 선택한 이력서를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자, 공고 또는 포지션을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 지원한 공고이거나 마감된 공고")
    })
    @PostMapping("/api/jobs/{jobId}/applications")
    public ApplicationResponse apply(@AuthenticationPrincipal OAuth2User user, @PathVariable Long jobId,
                                      @RequestBody ApplyRequest request) {
        return applyJobService.execute(user, jobId, request.jobPositionId(), request.resumeIds());
    }

    @Operation(summary = "내 지원 현황 조회", description = "현재 로그인한 사용자의 지원 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/api/applications")
    public List<ApplicationResponse> queryMyApplications(@AuthenticationPrincipal OAuth2User user) {
        return queryMyApplicationsService.execute(user);
    }

    @Operation(summary = "외부 지원 내역 등록", description = "잡코리아/원티드 등 사내 공식 공고가 아닌 외부 플랫폼에 지원한 내역을 학생 본인이 직접 등록합니다. 초기 상태는 지원 접수(APPLIED)로 고정되며, 이후 상태 변경은 관리자가 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 학생이 아닌 사용자"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/api/applications/external")
    public ApplicationResponse registerExternalApplication(@AuthenticationPrincipal OAuth2User user,
                                                             @RequestBody ExternalApplicationRequest request) {
        return registerMyExternalApplicationService.execute(user, request.companyName(), request.sourcePlatform());
    }

    @Operation(summary = "지원자 목록 조회", description = "전체 또는 특정 공고(jobPostingId=공고 ID)의 지원자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/applicants")
    public List<AdminApplicantResponse> queryApplicants(@RequestParam(required = false) Long jobPostingId) {
        return queryApplicantsService.execute(jobPostingId);
    }

    @Operation(summary = "지원자 수동 등록", description = "잡코리아/원티드 등 외부 플랫폼을 통해 지원한 학생의 지원 현황을 관리자가 직접 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 잘못된 지원 경로"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음")
    })
    @PostMapping("/api/admin/applicants")
    public AdminApplicantResponse registerManualApplicant(@RequestBody ManualApplicantRequest request) {
        return registerManualApplicantService.execute(
                request.userId(), request.companyName(), request.sourcePlatform(), request.status()
        );
    }

    @Operation(summary = "지원자 이력서 ZIP 다운로드", description = "전체 또는 특정 공고(companyId=공고 ID) 지원자의 이력서를 ZIP으로 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "지원자 없음")
    })
    @GetMapping("/api/admin/applicants/zip")
    public ResponseEntity<Resource> downloadApplicantsZip(@RequestParam(required = false) Long companyId) {
        return FileDownloadResponses.of(queryApplicantResumesZipService.execute(companyId));
    }

    @Operation(summary = "지원자 이력서 다운로드", description = "특정 지원 건에 첨부된 이력서 파일(들)을 ZIP으로 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "지원 내역 또는 이력서를 찾을 수 없음")
    })
    @GetMapping("/api/admin/applicants/{applicationId}/resume")
    public ResponseEntity<Resource> downloadApplicantResume(@PathVariable Long applicationId) {
        return FileDownloadResponses.of(queryApplicantResumeService.execute(applicationId));
    }

    @Operation(summary = "지원 결과 처리", description = "지원 건의 상태(면접 예정/최종 합격/면접 탈락 등)를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "지원 내역을 찾을 수 없음")
    })
    @PatchMapping("/api/admin/applicants/{applicationId}/result")
    public AdminApplicantResponse modifyApplicationResult(@PathVariable Long applicationId, @RequestBody ResultRequest request) {
        return modifyApplicationResultService.execute(applicationId, request.status());
    }

    private record ApplyRequest(Long jobPositionId, List<Long> resumeIds) {}

    private record ExternalApplicationRequest(String companyName, String sourcePlatform) {}

    private record ResultRequest(ApplicationStatus status) {}

    private record ManualApplicantRequest(
            Long userId,
            String companyName,
            String sourcePlatform,
            ApplicationStatus status
    ) {}
}