package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.global.model.SocialProvider;

public interface SocialLoginService {
    
    // 인증 로직 처리
    LoginResponse login(String idToken);
    
    // 서비스가 처리할 수 있는 제공자 반환
    SocialProvider getSocialProvider();

}
