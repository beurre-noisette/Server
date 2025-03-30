package hello.cokezet.temporary.domain.user.dto.request;

import hello.cokezet.temporary.global.model.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 연결 해제를 위한 요청 DTO
 * 회원탈퇴 시 소셜 플랫폼과의 연결도 해제하기 위해 필요한 정보가 포함됨
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 연결 해제 요청 DTO")
public class SocialRevokeRequest {

    @Schema(description = "소셜 로그인 제공자",
            examples = "GOOGLE",
            allowableValues = {"GOOGLE, APPLE"})
    private SocialProvider socialProvider;

    @Schema(description = "소셜 플랫폼에서 받은 ID 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String idToken;
}
