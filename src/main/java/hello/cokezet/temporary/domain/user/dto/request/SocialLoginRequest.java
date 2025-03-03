package hello.cokezet.temporary.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {

    private String idToken; // 소셜 플랫폼에서 받은 ID 토큰

    private String deviceType; // android, ios
    
    private String deviceInfo; // 기기 식별 정보 (하나의 계정에 하나의 로그인만 가능하게 할 예정)

}
