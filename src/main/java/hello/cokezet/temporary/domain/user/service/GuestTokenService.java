package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.global.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class GuestTokenService {

    private final JwtProvider jwtProvider;
    private final long GUEST_TOKEN_VALIDITY = 3 * 60 * 60 * 1000; // 3시간

    public GuestTokenService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse createGuestToken() {
        // UUID를 사용해 고유한 게스트 식별자 생성
        String guestId = UUID.randomUUID().toString().substring(0, 8);
        String guestEmail = "guest_" + guestId + "@temp.cokezet.com";
        
        // 임시 ID 값 (실제 DB에 저장하지 않음)
        long tempUserId = -1 * System.currentTimeMillis(); // 음수 ID로 실제 사용자와 구분

        String accessToken = jwtProvider.generateGuestAccessToken(tempUserId, guestEmail, GUEST_TOKEN_VALIDITY);

        log.info("게스트 토큰 생성: id = {}, email = {}", tempUserId, guestEmail);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(null)
                .user(new LoginResponse.UserInfo(
                        tempUserId,
                        guestEmail,
                        "Guest"
                ))
                .newUser(false)
                .build();
    }
}
