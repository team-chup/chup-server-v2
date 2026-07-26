package team.themoment.thup.domain.job.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Job", description = "채용공고 API")
@RequiredArgsConstructor
public class JobController {

    private final QueryJobPostingsService queryJobPostingsService;
    private final QueryJobPostingService queryJobPostingService;
    private final QueryAttachmentService queryAttachmentService;
    private final QueryAdminJobPostingsService queryAdminJobPostingsService;
    private final CreateJobPostingService createJobPostingService;
    private final ModifyJobPostingService modifyJobPostingService;
    private final ModifyJobPostingStatusService modifyJobPostingStatusService;

    @Operation(summary = "채용 공고 목록", description = "모집중/마감된 채용 공고 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/api/jobs")
    public List<JobPostingJpaEntity> queryJobPostings() {
        return queryJobPostingsService.execute();
    }

    @Operation(summary = "채용 공고 상세", description = "채용 공고 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "공고를 찾을 수 없음")
    })
    @GetMapping("/api/jobs/{jobId}")
    public JobPostingJpaEntity queryJobPosting(@PathVariable Long jobId) {
        return queryJobPostingService.execute(jobId);
    }

    @Operation(summary = "공고 첨부파일 다운로드", description = "채용 공고에 첨부된 파일 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "첨부파일을 찾을 수 없음")
    })
    @GetMapping("/api/jobs/{jobId}/attachments/{fileId}")
    public AttachmentJpaEntity downloadAttachment(@PathVariable Long jobId, @PathVariable Long fileId) {
        return queryAttachmentService.execute(jobId, fileId);
    }

    @Operation(summary = "관리자 공고 목록", description = "상태와 무관하게 전체 채용 공고 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/jobs")
    public List<JobPostingJpaEntity> queryAdminJobPostings() {
        return queryAdminJobPostingsService.execute();
    }

    @Operation(summary = "공고 등록", description = "채용 공고와 포지션을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/api/admin/jobs")
    public JobPostingJpaEntity createJobPosting(@AuthenticationPrincipal OAuth2User admin, @RequestBody JobPostingRequest request) {
        return createJobPostingService.execute(admin, request.companyName(), request.description(),
                request.employmentType(), request.recruitStart(), request.recruitEnd(), request.positionNames());
    }

    @Operation(summary = "공고 수정", description = "채용 공고 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "공고를 찾을 수 없음")
    })
    @PatchMapping("/api/admin/jobs/{jobId}")
    public JobPostingJpaEntity modifyJobPosting(@PathVariable Long jobId, @RequestBody JobPostingRequest request) {
        return modifyJobPostingService.execute(jobId, request.companyName(), request.description(),
                request.employmentType(), request.recruitStart(), request.recruitEnd());
    }

    @Operation(summary = "공고 상태 변경", description = "채용 공고의 모집 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "공고를 찾을 수 없음")
    })
    @PatchMapping("/api/admin/jobs/{jobId}/status")
    public JobPostingJpaEntity modifyJobPostingStatus(@PathVariable Long jobId, @RequestBody JobPostingStatusRequest request) {
        return modifyJobPostingStatusService.execute(jobId, request.status());
    }

    private record JobPostingRequest(String companyName, String description, EmploymentType employmentType,
                                      LocalDate recruitStart, LocalDate recruitEnd, List<String> positionNames) {}

    private record JobPostingStatusRequest(JobPostingStatus status) {}
}