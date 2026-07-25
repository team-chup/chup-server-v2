package team.themoment.thup.domain.user.controller;

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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryUserService queryUserService;
    private final ModifyUserPhoneNumberService modifyUserPhoneNumberService;
    private final UpsertResumeService upsertResumeService;

    @GetMapping("/me")
    public UserJpaEntity queryMe(@AuthenticationPrincipal OAuth2User user) {
        return queryUserService.execute(user);
    }

    @PatchMapping("/me")
    public UserJpaEntity modifyPhoneNumber(@AuthenticationPrincipal OAuth2User user, @RequestBody PhoneNumberRequest request) {
        return modifyUserPhoneNumberService.execute(user, request.phoneNumber());
    }

    @PutMapping("/me/resume")
    public ResumeJpaEntity upsertResume(@AuthenticationPrincipal OAuth2User user, @RequestBody ResumeRequest request) {
        return upsertResumeService.execute(user, request.fileName(), request.fileUrl(), request.fileSize());
    }

    private record PhoneNumberRequest(String phoneNumber) {}

    private record ResumeRequest(String fileName, String fileUrl, Integer fileSize) {}
}