package team.themoment.thup.domain.transportsubsidy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.thup.domain.transportsubsidy.dto.AdminTransportSubsidyResponse;
import team.themoment.thup.domain.transportsubsidy.dto.MyTransportSubsidyResponse;
import team.themoment.thup.domain.transportsubsidy.dto.TransportSubsidyStudentSummaryResponse;
import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;
import team.themoment.thup.domain.transportsubsidy.service.ModifyTransportSubsidyResultService;
import team.themoment.thup.domain.transportsubsidy.service.QueryMyTransportSubsidyApplicationsService;
import team.themoment.thup.domain.transportsubsidy.service.QueryTransportSubsidyApplicationsService;
import team.themoment.thup.domain.transportsubsidy.service.QueryTransportSubsidyEvidenceService;
import team.themoment.thup.domain.transportsubsidy.service.QueryTransportSubsidyStudentsService;
import team.themoment.thup.domain.transportsubsidy.service.RegisterTransportSubsidyApplicationService;
import team.themoment.thup.global.storage.FileDownloadResponses;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "TransportSubsidy", description = "면접 교통비 지원 API")
@RequiredArgsConstructor
public class TransportSubsidyController {

    private final RegisterTransportSubsidyApplicationService registerTransportSubsidyApplicationService;
    private final QueryMyTransportSubsidyApplicationsService queryMyTransportSubsidyApplicationsService;
    private final QueryTransportSubsidyStudentsService queryTransportSubsidyStudentsService;
    private final QueryTransportSubsidyApplicationsService queryTransportSubsidyApplicationsService;
    private final ModifyTransportSubsidyResultService modifyTransportSubsidyResultService;
    private final QueryTransportSubsidyEvidenceService queryTransportSubsidyEvidenceService;

    @Operation(summary = "면접 교통비 지원 신청", description = "면접 회사명, 일시와 증빙 서류(영수증, 교통편 예약 화면 등)를 첨부해 교통비 지원을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신청 성공"),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 잘못된 증빙 파일"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "413", description = "파일 용량 초과")
    })
    @PostMapping(value = "/api/transport-subsidies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MyTransportSubsidyResponse register(@AuthenticationPrincipal OAuth2User user,
                                                @RequestParam String companyName,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewAt,
                                                @RequestParam("files") List<MultipartFile> files) {
        return registerTransportSubsidyApplicationService.execute(user, companyName, interviewAt, files);
    }

    @Operation(summary = "내 면접 교통비 지원 신청 내역 조회", description = "현재 로그인한 학생의 교통비 지원 신청 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/api/transport-subsidies")
    public List<MyTransportSubsidyResponse> queryMyApplications(@AuthenticationPrincipal OAuth2User user) {
        return queryMyTransportSubsidyApplicationsService.execute(user);
    }

    @Operation(summary = "교통비 지원 대상 학생 목록 조회", description = "3학년 학생별 면접 교통비 지원 누적 승인/신청 횟수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/transport-subsidies/students")
    public List<TransportSubsidyStudentSummaryResponse> queryStudents() {
        return queryTransportSubsidyStudentsService.execute();
    }

    @Operation(summary = "면접 교통비 지원 신청 목록 조회", description = "전체 또는 특정 학생(userId=학생 ID)의 교통비 지원 신청 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/api/admin/transport-subsidies")
    public List<AdminTransportSubsidyResponse> queryApplications(@RequestParam(required = false) Long userId) {
        return queryTransportSubsidyApplicationsService.execute(userId);
    }

    @Operation(summary = "면접 교통비 지원 증빙 서류 다운로드", description = "특정 신청 건에 첨부된 증빙 서류(들)를 ZIP으로 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "신청 내역 또는 증빙 서류를 찾을 수 없음")
    })
    @GetMapping("/api/admin/transport-subsidies/{applicationId}/evidence")
    public ResponseEntity<Resource> downloadEvidence(@PathVariable Long applicationId) {
        return FileDownloadResponses.of(queryTransportSubsidyEvidenceService.execute(applicationId));
    }

    @Operation(summary = "면접 교통비 지원 승인/거절", description = "학생의 교통비 지원 신청을 승인 또는 거절합니다. 승인 시 해당 학생의 누적 승인 횟수가 1 증가하며, 이미 2회 승인된 경우 추가 승인은 거부됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 처리된 신청이거나 승인 횟수 초과")
    })
    @PatchMapping("/api/admin/transport-subsidies/{applicationId}/result")
    public AdminTransportSubsidyResponse modifyResult(@PathVariable Long applicationId, @RequestBody ResultRequest request) {
        return modifyTransportSubsidyResultService.execute(applicationId, request.status());
    }

    private record ResultRequest(TransportSubsidyStatus status) {}
}
