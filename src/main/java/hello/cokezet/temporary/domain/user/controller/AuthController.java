package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.LoginResponse;
import hello.cokezet.temporary.domain.user.dto.SocialLoginRequest;
import hello.cokezet.temporary.domain.user.service.GoogleLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleLoginService googleLoginService;

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody SocialLoginRequest request) {
        log.info("Google 로그인 요청: deviceType={}", request.getDeviceType());

        LoginResponse response = googleLoginService.login(request.getIdToken(), request.getDeviceType());

        return ResponseEntity.ok(response);
    }
}
