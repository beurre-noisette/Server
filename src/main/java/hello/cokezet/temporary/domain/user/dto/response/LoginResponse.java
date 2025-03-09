package hello.cokezet.temporary.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "리프레쉬 토큰", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "사용자 정보")
    private UserInfo user; // 사용자 정보

    @Schema(description = "신규 사용자 여부 (true: 신규 사용자, false: 기존 사용자)")
    private boolean newUser; // 신규 사용자 여부

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "사용자 기본 정보")
    public static class UserInfo {

        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "사용자 이메일")
        private String email;

        @Schema(description = "사용자 닉네임")
        private String nickname;

    }
}
