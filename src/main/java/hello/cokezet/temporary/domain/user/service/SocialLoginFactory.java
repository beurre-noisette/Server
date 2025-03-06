package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.global.error.exception.UnsupportedSocialTypeException;
import hello.cokezet.temporary.global.model.SocialProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SocialLoginFactory {

    private final Set<SocialLoginService> loginServices;

    private Map<SocialProvider, SocialLoginService> getLoginServiceMap() {
        return loginServices.stream()
                .collect(Collectors.toMap(
                        SocialLoginService::getSocialProvider,
                        Function.identity()
                ));
    }

    public SocialLoginService getLoginService(SocialProvider socialProvider) {
        SocialLoginService loginService = getLoginServiceMap().get(socialProvider);

        if (loginService == null) {
            throw new UnsupportedSocialTypeException("지원하지 않는 소셜 로그인입니다. : " + socialProvider);
        }

        return loginService;
    }
}
