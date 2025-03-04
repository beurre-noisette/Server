package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.dto.request.RefreshTokenRequest;
import hello.cokezet.temporary.domain.user.dto.response.RefreshTokenResponse;
import hello.cokezet.temporary.domain.user.dto.request.SocialLoginRequest;
import hello.cokezet.temporary.domain.user.service.GoogleLoginService;
import hello.cokezet.temporary.domain.user.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final GoogleLoginService googleLoginService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody SocialLoginRequest request) {
        log.info("Google 로그인 요청: deviceType={}, deviceInfo={}", request.getDeviceType(), request.getDeviceInfo());

        LoginResponse response = googleLoginService.login(request.getIdToken(), request.getDeviceType(), request.getDeviceInfo());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody RefreshTokenRequest request,
            @RequestHeader("User-Device-Info") String deviceInfo) {

        RefreshTokenResponse response = refreshTokenService.refreshAccessToken(request.getRefreshToken(), deviceInfo);

        return ResponseEntity.ok(response);
    }
}
