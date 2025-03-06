package hello.cokezet.temporary.domain.user.dto.request;

import hello.cokezet.temporary.global.model.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {

    private String idToken; // 소셜 플랫폼에서 받은 ID 토큰

    private SocialProvider provider; // GOOGLE, APPLE, KAKAO
    
}
