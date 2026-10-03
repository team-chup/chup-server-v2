package team.themoment.thup.domain.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import team.themoment.thup.domain.notice.dto.NoticeDetailResponse;
import team.themoment.thup.domain.notice.dto.NoticeSummaryResponse;
import team.themoment.thup.domain.notice.service.CreateNoticeService;
import team.themoment.thup.domain.notice.service.DeleteNoticeService;
import team.themoment.thup.domain.notice.service.ModifyNoticeService;
import team.themoment.thup.domain.notice.service.QueryNoticeService;
import team.themoment.thup.domain.notice.service.QueryNoticesService;

import java.util.List;

@RestController
@Tag(name = "Notice", description = "취업 공지사항 API")
@RequiredArgsConstructor
public class NoticeController {

    private final QueryNoticesService queryNoticesService;
    private final QueryNoticeService queryNoticeService;
    private final CreateNoticeService createNoticeService;
    private final ModifyNoticeService modifyNoticeService;
    private final DeleteNoticeService deleteNoticeService;

    @Operation(summary = "공지사항 목록", description = "취업 공지사항 목록을 최신순으로 조회합니다. 학생과 관리자 모두 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/api/notices")
    public List<NoticeSummaryResponse> queryNotices() {
        return queryNoticesService.execute();
    }

    @Operation(summary = "공지사항 상세", description = "취업 공지사항의 제목, 내용, 작성자, 작성/수정 일시를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @GetMapping("/api/notices/{noticeId}")
    public NoticeDetailResponse queryNotice(@PathVariable Long noticeId) {
        return queryNoticeService.execute(noticeId);
    }

    @Operation(summary = "공지사항 등록", description = "취업 공지사항을 등록합니다(제목 최대 100자, 내용 최대 10000자). 등록 시 디스코드 채용 알림 채널로 공지사항 바로가기 링크가 포함된 알림을 보냅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "제목/내용 누락 또는 길이 초과"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/api/admin/notices")
    public NoticeDetailResponse createNotice(@AuthenticationPrincipal OAuth2User admin, @RequestBody NoticeRequest request) {
        return createNoticeService.execute(admin, request.title(), request.content());
    }

    @Operation(summary = "공지사항 수정", description = "취업 공지사항의 제목과 내용을 수정합니다. 수정 시에는 디스코드 알림을 보내지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "제목/내용 누락 또는 길이 초과"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @PatchMapping("/api/admin/notices/{noticeId}")
    public NoticeDetailResponse modifyNotice(@PathVariable Long noticeId, @RequestBody NoticeRequest request) {
        return modifyNoticeService.execute(noticeId, request.title(), request.content());
    }

    @Operation(summary = "공지사항 삭제", description = "취업 공지사항을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @DeleteMapping("/api/admin/notices/{noticeId}")
    public void deleteNotice(@PathVariable Long noticeId) {
        deleteNoticeService.execute(noticeId);
    }

    private record NoticeRequest(String title, String content) {}
}
