package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;

public interface SocialLoginService {

    LoginResponse login(String idToken, String deviceType);

}
