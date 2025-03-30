package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.request.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.dto.request.SocialRevokeRequest;
import hello.cokezet.temporary.domain.user.dto.response.ProfileResponse;
import hello.cokezet.temporary.domain.user.service.UserProfileService;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.error.ErrorCode;
import hello.cokezet.temporary.global.error.exception.BusinessException;
import hello.cokezet.temporary.global.model.SocialProvider;
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

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 탈퇴 처리합니다. 소셜 연결도 함께 해제됩니다."
    )
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResult<Void>> deleteAccount(@AuthenticationPrincipal UserPrincipal principal, @RequestBody SocialRevokeRequest request) {
        log.info("회원탈퇴 요청: userId = {}, provider = {}", principal.getId(), request.getSocialProvider());

        // 필수 입력값 검증
        if (request.getSocialProvider() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "소셜 토큰과 제공자 정보는 필수입니다.");
        }

        // 연결 해제 토큰(revokeToken) 검증
        if (request.getRevokeToken() == null || request.getRevokeToken().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    request.getSocialProvider() == SocialProvider.GOOGLE
                            ? "구글 연결 해제를 위한 액세스 토큰은 필수입니다."
                            : "애플 연결 해제를 위한 리프레시 토큰은 필수입니다.");
        }

        userProfileService.deleteUserAccount(
                principal.getId(),
                request.getSocialProvider(),
                request.getRevokeToken()
        );

        return ResponseEntity.ok(ApiResult.success(null));
    }

    @GetMapping("/profile/status")
    public ResponseEntity<ApiResult<Map<String, Boolean>>> checkProfileStatus(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isComplete = userProfileService.isProfileComplete(principal.getId());

        Map<String, Boolean> statusMap = Map.of("isComplete", isComplete);

        return ResponseEntity.ok(ApiResult.success(statusMap));
    }
}
