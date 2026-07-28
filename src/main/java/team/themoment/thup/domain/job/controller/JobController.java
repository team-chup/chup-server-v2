package team.themoment.thup.domain.job.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.thup.domain.job.dto.AdminJobPostingResponse;
import team.themoment.thup.domain.job.dto.JobPostingDetailResponse;
import team.themoment.thup.domain.job.dto.JobPostingSummaryResponse;
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
import team.themoment.thup.global.storage.FileDownloadResponses;

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

    @Operation(summary = "채용 공고 목록", description = "모집중인 채용 공고 목록을 검색/필터/정렬하여 조회합니다. 공고별 모집 포지션 목록을 함께 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/api/jobs")
    public List<JobPostingSummaryResponse> queryJobPostings(@RequestParam(required = false) String q,
                                                              @RequestParam(required = false) String position,
                                                              @RequestParam(required = false) EmploymentType employmentType,
                                                              @RequestParam(required = false) String sort) {
        return queryJobPostingsService.execute(q, position, employmentType, sort);
    }

    @Operation(summary = "채용 공고 상세", description = "채용 공고 상세 정보를 조회합니다. 포지션 목록(id, name)과 첨부파일 목록(id, fileName)을 함께 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "공고를 찾을 수 없음")
    })
    @GetMapping("/api/jobs/{jobId}")
    public JobPostingDetailResponse queryJobPosting(@PathVariable Long jobId) {
        return queryJobPostingService.execute(jobId);
    }

    @Operation(summary = "공고 첨부파일 다운로드", description = "채용 공고에 첨부된 파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "첨부파일을 찾을 수 없음")
    })
    @GetMapping("/api/jobs/{jobId}/attachments/{fileId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long jobId, @PathVariable Long fileId) {
        return FileDownloadResponses.of(queryAttachmentService.execute(jobId, fileId));
    }

    @Operation(summary = "관리자 공고 목록", description = "상태와 무관하게 전체 채용 공고 목록을 회사명으로 검색하여 조회합니다. 공고별 지원자 수를 함께 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/jobs")
    public List<AdminJobPostingResponse> queryAdminJobPostings(@RequestParam(required = false) String q) {
        return queryAdminJobPostingsService.execute(q);
    }

    @Operation(summary = "공고 등록", description = "채용 공고와 포지션, 첨부파일(최대 5개, PDF/HWP/HWPX/이미지)을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락 또는 지원하지 않는 첨부파일 형식"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "413", description = "첨부파일 개수·용량 초과")
    })
    @PostMapping(value = "/api/admin/jobs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JobPostingJpaEntity createJobPosting(@AuthenticationPrincipal OAuth2User admin,
                                                 @RequestParam String companyName,
                                                 @RequestParam String description,
                                                 @RequestParam EmploymentType employmentType,
                                                 @RequestParam LocalDate recruitStart,
                                                 @RequestParam LocalDate recruitEnd,
                                                 @RequestParam List<String> positionNames,
                                                 @RequestParam(required = false) List<MultipartFile> attachments) {
        return createJobPostingService.execute(admin, companyName, description, employmentType,
                recruitStart, recruitEnd, positionNames, attachments);
    }

    @Operation(summary = "공고 수정", description = "채용 공고 정보를 수정합니다. positionNames를 보내면 포지션 목록을 동기화하고(신규 추가/미포함분 삭제), 지원자가 있는 포지션은 삭제하지 않고 409를 반환합니다. 첨부파일을 함께 보내면 기존 첨부파일을 전량 교체합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 첨부파일 형식"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "공고를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "지원자가 있는 포지션을 삭제하려 함"),
            @ApiResponse(responseCode = "413", description = "첨부파일 개수·용량 초과")
    })
    @PatchMapping(value = "/api/admin/jobs/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JobPostingJpaEntity modifyJobPosting(@PathVariable Long jobId,
                                                 @RequestParam String companyName,
                                                 @RequestParam String description,
                                                 @RequestParam EmploymentType employmentType,
                                                 @RequestParam LocalDate recruitStart,
                                                 @RequestParam LocalDate recruitEnd,
                                                 @RequestParam(required = false) List<String> positionNames,
                                                 @RequestParam(required = false) List<MultipartFile> attachments) {
        return modifyJobPostingService.execute(jobId, companyName, description, employmentType,
                recruitStart, recruitEnd, positionNames, attachments);
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

    private record JobPostingStatusRequest(JobPostingStatus status) {}
}