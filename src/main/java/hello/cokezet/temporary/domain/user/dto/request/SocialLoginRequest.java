package hello.cokezet.temporary.domain.user.dto.request;

import hello.cokezet.temporary.global.model.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 로그인 요청 DTO")
public class SocialLoginRequest {

    @Schema(description = "소셜 플랫폼에서 받은 ID 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
            required = true)
    private String idToken; // 소셜 플랫폼에서 받은 ID 토큰

    @Schema(description = "소셜 로그인 제공자",
            example = "GOOGLE",
            required = true,
            allowableValues = {"GOOGLE", "APPLE", "KAKAO"})
    private SocialProvider provider; // GOOGLE, APPLE, KAKAO

}
