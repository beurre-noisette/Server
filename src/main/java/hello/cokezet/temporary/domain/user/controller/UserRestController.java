package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.request.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.dto.response.ProfileResponse;
import hello.cokezet.temporary.domain.user.service.UserProfileService;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.security.jwt.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/users")
@Tag(name = "유저", description = "유저 프로필 관리 API")
public class UserRestController {

    private final UserProfileService userProfileService;

    public UserRestController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(
            summary = "프로필 업데이트",
            description = "사용자의 프로필 정보를 업데이트합니다. 최초 설정 시에는 선호커머스와 카드사를 반드시 설정해야 합니다."
    )
    @PostMapping("/profile")
    public ResponseEntity<ApiResult<Void>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileUpdateRequest request
    ) {

        log.info("프로필 업데이트 요청: userId = {}", principal.getId());

        userProfileService.updateUserProfile(principal, request);

        return ResponseEntity.ok(ApiResult.success(null));

    }

    @Operation(
            summary = "프로필 조회",
            description = "사용자의 프로필 정보를 조회합니다."
    )
    @GetMapping("/profile")
    public ResponseEntity<ApiResult<ProfileResponse>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {

        log.info("프로필 조회 요청: userId = {}", principal.getId());

        ProfileResponse response = userProfileService.getUserProfile(principal.getId());

        return ResponseEntity.ok(ApiResult.success(response));
    }

    @GetMapping("/profile/status")
    public ResponseEntity<ApiResult<Map<String, Boolean>>> checkProfileStatus(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isComplete = userProfileService.isProfileComplete(principal.getId());

        Map<String, Boolean> statusMap = Map.of("isComplete", isComplete);

        return ResponseEntity.ok(ApiResult.success(statusMap));
    }
}
