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
        log.info("Google 로그인 요청: deviceType={}", request.getDeviceType());

        LoginResponse response = googleLoginService.login(request.getIdToken(), request.getDeviceType());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = refreshTokenService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(response);
    }
}
