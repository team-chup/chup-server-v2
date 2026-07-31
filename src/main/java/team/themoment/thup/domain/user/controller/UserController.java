package team.themoment.thup.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.thup.domain.user.dto.ResumeResponse;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.service.DeleteResumeService;
import team.themoment.thup.domain.user.service.ModifyUserPhoneNumberService;
import team.themoment.thup.domain.user.service.QueryResumesService;
import team.themoment.thup.domain.user.service.QueryUserService;
import team.themoment.thup.domain.user.service.RegisterResumeService;

import java.util.List;

@RestController
@Tag(name = "User", description = "유저 API")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryUserService queryUserService;
    private final ModifyUserPhoneNumberService modifyUserPhoneNumberService;
    private final RegisterResumeService registerResumeService;
    private final QueryResumesService queryResumesService;
    private final DeleteResumeService deleteResumeService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public UserJpaEntity queryMe(@AuthenticationPrincipal OAuth2User user) {
        return queryUserService.execute(user);
    }

    @Operation(summary = "전화번호 수정", description = "현재 로그인한 사용자의 전화번호를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/me")
    public UserJpaEntity modifyPhoneNumber(@AuthenticationPrincipal OAuth2User user, @RequestBody PhoneNumberRequest request) {
        return modifyUserPhoneNumberService.execute(user, request.phoneNumber());
    }

    @Operation(summary = "이력서 등록", description = "현재 로그인한 사용자의 PDF 이력서를 등록합니다. 최대 3개까지 등록할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "PDF 파일이 아님"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 3개 등록됨"),
            @ApiResponse(responseCode = "413", description = "파일 용량 초과")
    })
    @PostMapping(value = "/me/resumes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeResponse registerResume(@AuthenticationPrincipal OAuth2User user, @RequestParam("file") MultipartFile file) {
        return registerResumeService.execute(user, file);
    }

    @Operation(summary = "이력서 목록 조회", description = "현재 로그인한 사용자가 등록한 이력서 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/me/resumes")
    public List<ResumeResponse> queryResumes(@AuthenticationPrincipal OAuth2User user) {
        return queryResumesService.execute(user);
    }

    @Operation(summary = "이력서 삭제", description = "현재 로그인한 사용자가 등록한 이력서 중 하나를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "등록된 이력서를 찾을 수 없음")
    })
    @DeleteMapping("/me/resumes/{resumeId}")
    public void deleteResume(@AuthenticationPrincipal OAuth2User user, @PathVariable Long resumeId) {
        deleteResumeService.execute(user, resumeId);
    }

    private record PhoneNumberRequest(String phoneNumber) {}
}