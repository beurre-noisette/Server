package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.service.GuestTokenService;
import hello.cokezet.temporary.global.common.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/guest")
@Tag(name = "게스트", description = "비회원 인증 API")
public class GuestRestController {

    private final GuestTokenService guestTokenService;

    public GuestRestController(GuestTokenService guestTokenService) {
        this.guestTokenService = guestTokenService;
    }

    @Operation(
            summary = "게스트 토큰 발급",
            description = "비회원 사용자를 위한 제한된 권한의 토큰을 발급합니다. (유효시간 3시간)"
    )
    @PostMapping("/token")
    public ResponseEntity<ApiResult<LoginResponse>> getGuestToken() {
        log.info("게스트 토큰 요청");

        LoginResponse response = guestTokenService.createGuestToken();

        return ResponseEntity.ok(ApiResult.success(response));
    }
}
