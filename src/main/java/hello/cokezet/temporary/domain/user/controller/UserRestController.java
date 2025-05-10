package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.request.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.dto.request.SocialRevokeRequest;
import hello.cokezet.temporary.domain.user.dto.response.ProfileResponse;
import hello.cokezet.temporary.domain.user.service.UserProfileService;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.security.jwt.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            description = "사용자의 프로필 정보를 업데이트합니다. 최초 설정 시에는 선호커머스와 카드사를 반드시 설정해야 합니다.",
            tags = {"유저"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "프로필 업데이트 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "프로필 업데이트 실패 - 유효하지 않은 입력값",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class),
                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            value = "{\"success\":false,\"error\":{\"code\":\"PROFILE-001\",\"message\":\"프로필 업데이트에 실패했습니다\"}}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    )
            }
    )
    @PostMapping("/profile")
    public ResponseEntity<ApiResult<Void>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @ParameterObject @RequestBody ProfileUpdateRequest request
    ) {

        log.info("프로필 업데이트 요청: userId = {}", principal.getId());

        userProfileService.updateUserProfile(principal, request);

        return ResponseEntity.ok(ApiResult.success(null));

    }

    @Operation(
            summary = "프로필 조회",
            description = "사용자의 프로필 정보를 조회합니다. 사용자 기본 정보, 선호 커머스, 선호 카드사 정보를 포함합니다.",
            tags = {"유저"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "프로필 조회 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class),
                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            value = "{\"success\":true,\"data\":{\"id\":1,\"email\":\"user@example.com\",\"nickname\":\"사용자\",\"profileComplete\":true,\"preferredCommerces\":[{\"id\":1,\"name\":\"쿠팡\"}],\"preferredCardCompanies\":[{\"id\":1,\"name\":\"신한카드\"}]}}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자를 찾을 수 없음",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class),
                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            value = "{\"success\":false,\"error\":{\"code\":\"USER-001\",\"message\":\"사용자를 찾을 수 없습니다\"}}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/profile")
    public ResponseEntity<ApiResult<ProfileResponse>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {

        log.info("프로필 조회 요청: userId = {}", principal.getId());

        ProfileResponse response = userProfileService.getUserProfile(principal.getId());

        return ResponseEntity.ok(ApiResult.success(response));
    }

    /**
     * 기존 회원 탈퇴 메서드 (소셜 연결 해제 없음)
     * 소셜 연결 해제 기능이 추가된 새 메서드로 대체하고자 함.
     */
    /*
    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 탈퇴 처리합니다."
    )
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResult<Void>> deleteAccount(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("회원탈퇴 요청: userId = {}", principal.getId());

        userProfileService.deleteUserAccount(principal.getId());

        return ResponseEntity.ok(ApiResult.success(null));
    }
    */

    @Operation(
            summary = "회원 탈퇴 (소셜 연결 해제 포함)",
            description = "현재 로그인한 사용자의 계정을 탈퇴 처리하고, 연결된 소셜 계정도 함께 해제합니다. 소셜 연결 해제가 실패하면 회원 탈퇴도 취소됩니다.",
            tags = {"유저"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "회원 탈퇴 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "회원 탈퇴 실패 - 소셜 연결 해제 오류",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class),
                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            value = "{\"success\":false,\"error\":{\"code\":\"SOCIAL-004\",\"message\":\"소셜 계정 연결 해제 실패로 회원 탈퇴가 취소되었습니다. 실패한 플랫폼: GOOGLE\"}}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    )
            }
    )
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResult<Void>> deleteAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "소셜 연결 해제 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SocialRevokeRequest.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Google 계정 연결 해제",
                                            summary = "Google 계정 연결 해제 요청 예시",
                                            value = "{\"socialProvider\":\"GOOGLE\",\"revokeToken\":\"ya29.a0Ad...\"}"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Apple 계정 연결 해제",
                                            summary = "Apple 계정 연결 해제 요청 예시",
                                            value = "{\"socialProvider\":\"APPLE\",\"revokeToken\":\"ra0.a1bc...\"}"
                                    )
                            }
                    )
            )
            @RequestBody SocialRevokeRequest revokeRequest) {
        log.info("회원탈퇴 요청 (소셜 연결 해제 포함): userId = {}, provider = {}", 
                principal.getId(), revokeRequest.getSocialProvider());

        // 단일 소셜 계정 연결 해제 요청을 리스트로 변환하여 서비스에 전달
        List<SocialRevokeRequest> revokeTokens = List.of(revokeRequest);
        userProfileService.deleteUserAccount(principal.getId(), revokeTokens);

        return ResponseEntity.ok(ApiResult.success(null));
    }

    @Operation(
            summary = "프로필 완성 상태 확인",
            description = "사용자의 프로필이 완성되었는지 여부를 확인합니다. 프로필이 완성되었다면 isComplete가 true를 반환합니다.",
            tags = {"유저"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "프로필 상태 확인 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class),
                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            value = "{\"success\":true,\"data\":{\"isComplete\":true}}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResult.class)
                            )
                    )
            }
    )
    @GetMapping("/profile/status")
    public ResponseEntity<ApiResult<Map<String, Boolean>>> checkProfileStatus(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isComplete = userProfileService.isProfileComplete(principal.getId());

        Map<String, Boolean> statusMap = Map.of("isComplete", isComplete);

        return ResponseEntity.ok(ApiResult.success(statusMap));
    }
}
