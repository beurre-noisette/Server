package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.ProfileResponse;
import hello.cokezet.temporary.domain.user.dto.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.service.UserProfileService;
import hello.cokezet.temporary.global.security.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserProfileService userProfileService;

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileUpdateRequest request
    ) {

        log.info("프로필 업데이트 요청: userId = {}", principal.getId());

        userProfileService.updateUserProfile(principal, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {

        log.info("프로필 조회 요청: userId = {}", principal.getId());

        ProfileResponse response = userProfileService.getUserProfile(principal.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/status")
    public ResponseEntity<Map<String, Boolean>> checkProfileStatus(@AuthenticationPrincipal UserPrincipal principal) {

        boolean isComplete = userProfileService.isProfileComplete(principal.getId());

        return ResponseEntity.ok(Map.of("isComplete", isComplete));
    }
}
