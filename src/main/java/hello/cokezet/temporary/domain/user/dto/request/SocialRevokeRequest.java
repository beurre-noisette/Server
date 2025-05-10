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
@Schema(
        description = "소셜 연결 해제 요청 DTO",
        title = "SocialRevokeRequest",
        requiredProperties = {"socialProvider", "revokeToken"}
)
public class SocialRevokeRequest {

    @Schema(
            description = "소셜 로그인 제공자 (GOOGLE 또는 APPLE)",
            type = "string",
            allowableValues = {"GOOGLE", "APPLE"},
            example = "GOOGLE"
    )
    private SocialProvider socialProvider;

    @Schema(
            description = "소셜 플랫폼 연결 해제에 필요한 토큰. " +
                    "Google의 경우 액세스 토큰(Access Token)을, " +
                    "Apple의 경우 리프레시 토큰(Refresh Token)을 제공해야 합니다. " +
                    "이 토큰은 클라이언트에서 소셜 로그인 과정에서 받은 토큰을 사용합니다.",
            type = "string",
            example = "ya29.a0AfB_byDXozBy..."
    )
    private String revokeToken;
}
