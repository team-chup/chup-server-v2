package team.themoment.thup.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.thup.domain.user.dto.PendingAdminResDto;
import team.themoment.thup.domain.user.dto.UserSearchResDto;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.entity.constant.Role;
import team.themoment.thup.domain.user.service.ApproveAdminService;
import team.themoment.thup.domain.user.service.PromoteToAdminService;
import team.themoment.thup.domain.user.service.QueryPendingAdminsService;
import team.themoment.thup.domain.user.service.QueryUsersService;
import team.themoment.thup.domain.user.service.SwitchUserRoleService;

import java.util.List;

@RestController
@Tag(name = "User", description = "유저 API")
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final QueryPendingAdminsService queryPendingAdminsService;
    private final PromoteToAdminService promoteToAdminService;
    private final ApproveAdminService approveAdminService;
    private final QueryUsersService queryUsersService;
    private final SwitchUserRoleService switchUserRoleService;

    @Operation(summary = "사용자 검색", description = "역할(role)과 이름/학번 검색어(q)로 사용자를 검색합니다. 파라미터를 생략하면 해당 조건 없이 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping
    public List<UserSearchResDto> searchUsers(@RequestParam(required = false) Role role,
                                               @RequestParam(required = false) String q) {
        return queryUsersService.execute(role, q);
    }

    @Operation(summary = "관리자 승인 대기 목록", description = "승인 대기 중인 관리자 계정 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/pending")
    public List<PendingAdminResDto> queryPendingAdmins() {
        return queryPendingAdminsService.execute();
    }

    @Operation(summary = "관리자로 지정", description = "학생 계정을 관리자로 지정합니다. 승인 전까지는 관리자 권한이 부여되지 않으며, 대상자가 재로그인해야 승인 결과가 반영됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 관리자인 사용자")
    })
    @PatchMapping("/{userId}/promote")
    public UserJpaEntity promoteToAdmin(@AuthenticationPrincipal OAuth2User admin, @PathVariable Long userId) {
        return promoteToAdminService.execute(admin, userId);
    }

    @Operation(summary = "관리자 승인", description = "대기 중인 관리자 계정을 승인합니다. 승인 후 대상자가 재로그인해야 관리자 권한이 반영됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "400", description = "관리자 승인 대상이 아님"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 승인된 관리자")
    })
    @PatchMapping("/{userId}/approve")
    public UserJpaEntity approveAdmin(@AuthenticationPrincipal OAuth2User admin, @PathVariable Long userId) {
        return approveAdminService.execute(admin, userId);
    }

    @Operation(summary = "[개발용] 역할 강제 전환", description = "승인 절차 없이 대상 사용자의 역할을 즉시 STUDENT/ADMIN으로 전환합니다. 개발 환경(dev-tools.enabled=true)에서만 동작하며, 반영을 위해 대상자는 재로그인해야 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전환 성공"),
            @ApiResponse(responseCode = "400", description = "역할 값 누락"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없거나 개발 환경이 아님")
    })
    @PatchMapping("/{userId}/dev-role")
    public UserJpaEntity switchRole(@AuthenticationPrincipal OAuth2User admin, @PathVariable Long userId,
                                     @RequestBody SwitchRoleRequest request) {
        return switchUserRoleService.execute(admin, userId, request.role());
    }

    private record SwitchRoleRequest(Role role) {}
}
