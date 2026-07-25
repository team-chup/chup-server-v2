package team.themoment.thup.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import team.themoment.thup.domain.user.entity.ResumeJpaEntity;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.service.ModifyUserPhoneNumberService;
import team.themoment.thup.domain.user.service.QueryUserService;
import team.themoment.thup.domain.user.service.UpsertResumeService;

@RestController
@Tag(name = "User", description = "유저 API")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryUserService queryUserService;
    private final ModifyUserPhoneNumberService modifyUserPhoneNumberService;
    private final UpsertResumeService upsertResumeService;

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

    @Operation(summary = "이력서 등록/재등록", description = "현재 로그인한 사용자의 이력서를 등록하거나 재등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/me/resume")
    public ResumeJpaEntity upsertResume(@AuthenticationPrincipal OAuth2User user, @RequestBody ResumeRequest request) {
        return upsertResumeService.execute(user, request.fileName(), request.fileUrl(), request.fileSize());
    }

    private record PhoneNumberRequest(String phoneNumber) {}

    private record ResumeRequest(String fileName, String fileUrl, Integer fileSize) {}
}