package team.themoment.thup.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.service.ApproveAdminService;
import team.themoment.thup.domain.user.service.PromoteToAdminService;
import team.themoment.thup.domain.user.service.QueryPendingAdminsService;

import java.util.List;

@RestController
@Tag(name = "User", description = "유저 API")
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final QueryPendingAdminsService queryPendingAdminsService;
    private final PromoteToAdminService promoteToAdminService;
    private final ApproveAdminService approveAdminService;

    @Operation(summary = "관리자 승인 대기 목록", description = "승인 대기 중인 관리자 계정 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/pending")
    public List<UserJpaEntity> queryPendingAdmins() {
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
    public UserJpaEntity promoteToAdmin(@PathVariable Long userId) {
        return promoteToAdminService.execute(userId);
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
    public UserJpaEntity approveAdmin(@PathVariable Long userId) {
        return approveAdminService.execute(userId);
    }
}
