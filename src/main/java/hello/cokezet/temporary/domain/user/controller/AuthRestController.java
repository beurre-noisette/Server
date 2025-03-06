package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.dto.request.RefreshTokenRequest;
import hello.cokezet.temporary.domain.user.dto.response.RefreshTokenResponse;
import hello.cokezet.temporary.domain.user.dto.request.SocialLoginRequest;
import hello.cokezet.temporary.domain.user.service.GoogleLoginService;
import hello.cokezet.temporary.domain.user.service.RefreshTokenService;
import hello.cokezet.temporary.domain.user.service.SocialLoginFactory;
import hello.cokezet.temporary.domain.user.service.SocialLoginService;
import hello.cokezet.temporary.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final SocialLoginFactory socialLoginFactory;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> socialLogin(@RequestBody SocialLoginRequest request) {
        log.info("로그인 요청: provider={}", request.getProvider());

        SocialLoginService loginService = socialLoginFactory.getLoginService(request.getProvider());

        LoginResponse response = loginService.login(request.getIdToken());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = refreshTokenService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
