package hello.cokezet.temporary.domain.user.controller;

import hello.cokezet.temporary.domain.user.service.UserManagementService;
import hello.cokezet.temporary.global.common.ApiResult;
import hello.cokezet.temporary.global.model.SocialProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Profile("!prod") // 운영 환경에서는 접근 불가
@Tag(name = "테스트", description = "소셜 로그인 테스트를 위한 API")
public class TestRestController {

    private final UserManagementService userManagementService;

    @Operation(
            summary = "구글 로그인 사용자 로그아웃",
            description = "구글 로그인으로 인증된 모든 사용자의 토큰을 삭제합니다."
    )
    @PostMapping("/logout/google")
    public ResponseEntity<ApiResult<Void>> logoutGoogleUsers() {
        log.info("구글 사용자 로그아웃 요청");
        userManagementService.logoutUsersByProvider(SocialProvider.GOOGLE);
        return ResponseEntity.ok(ApiResult.success(null));
    }

    @Operation(
            summary = "애플 로그인 사용자 로그아웃",
            description = "애플 로그인으로 인증된 모든 사용자의 토큰을 삭제합니다."
    )
    @PostMapping("/logout/apple")
    public ResponseEntity<ApiResult<Void>> logoutAppleUsers() {
        log.info("애플 사용자 로그아웃 요청");
        userManagementService.logoutUsersByProvider(SocialProvider.APPLE);
        return ResponseEntity.ok(ApiResult.success(null));
    }

    @Operation(
            summary = "구글 로그인 사용자 삭제",
            description = "구글 로그인으로 가입한 모든 사용자와 관련 데이터를 삭제합니다."
    )
    @DeleteMapping("/users/google")
    public ResponseEntity<ApiResult<Void>> deleteGoogleUsers() {
        log.info("구글 사용자 삭제 요청");
        int count = userManagementService.deleteUsersByProvider(SocialProvider.GOOGLE);
        log.info("삭제된 구글 사용자 수: {}", count);
        return ResponseEntity.ok(ApiResult.success(null));
    }

    @Operation(
            summary = "애플 로그인 사용자 삭제",
            description = "애플 로그인으로 가입한 모든 사용자와 관련 데이터를 삭제합니다."
    )
    @DeleteMapping("/users/apple")
    public ResponseEntity<ApiResult<Void>> deleteAppleUsers() {
        log.info("애플 사용자 삭제 요청");
        int count = userManagementService.deleteUsersByProvider(SocialProvider.APPLE);
        log.info("삭제된 애플 사용자 수: {}", count);
        return ResponseEntity.ok(ApiResult.success(null));
    }

    @Operation(
            summary = "특정 이메일 사용자 삭제",
            description = "지정한 이메일의 사용자와 관련 데이터를 삭제합니다."
    )
    @DeleteMapping("/users")
    public ResponseEntity<ApiResult<Void>> deleteUserByEmail(@RequestParam String email) {
        log.info("이메일 사용자 삭제 요청: {}", email);
        userManagementService.deleteUserByEmail(email);
        return ResponseEntity.ok(ApiResult.success(null));
    }
}