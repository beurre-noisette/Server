package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.request.RefreshTokenRequest;
import hello.cokezet.temporary.domain.user.dto.request.SocialLoginRequest;
import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.dto.response.RefreshTokenResponse;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.domain.user.service.RefreshTokenService;
import hello.cokezet.temporary.domain.user.service.SocialLoginFactory;
import hello.cokezet.temporary.domain.user.service.SocialLoginService;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.error.exception.UserNotFoundException;
import hello.cokezet.temporary.global.security.jwt.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "소셜 로그인 및 토큰 관리 API")
public class AuthRestController {

    private final SocialLoginFactory socialLoginFactory;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthRestController(SocialLoginFactory socialLoginFactory, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.socialLoginFactory = socialLoginFactory;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "소셜 로그인",
            description = "Google 또는 Apple의 ID 토큰을 사용하여 인증하고 서버측의 JWT 토큰(Access, Refresh)을 발급받습니다."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> socialLogin(@RequestBody SocialLoginRequest request) {
        log.info("로그인 요청: provider={}", request.getProvider());

        SocialLoginService loginService = socialLoginFactory.getLoginService(request.getProvider());

        LoginResponse response = loginService.login(request.getIdToken());

        return ResponseEntity.ok(ApiResult.success(response));
    }

    @Operation(
            summary = "토큰 검증",
            description = "accessToken의 유효성을 검증하고 사용자 정보를 반환합니다. 자동 로그인에 사용됩니다."
    )
    @GetMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse.UserInfo>> validateToken(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("토큰 검증 요청: userId={}", principal.getId());

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        return ResponseEntity.ok(ApiResult.success(userInfo));
    }

    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = refreshTokenService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResult.success(response));
    }

    // 수정가능성 있음
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인한 사용자의 토큰을 무효화합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("로그아웃 요청: userId={}", principal.getId());

        refreshTokenService.logout(principal.getId());

        return ResponseEntity.ok(ApiResult.success(null));
    }
}
