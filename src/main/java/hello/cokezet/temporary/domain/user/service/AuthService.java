package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.model.RefreshToken;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.global.error.exception.UserNotFoundException;
import hello.cokezet.temporary.global.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, JwtProvider jwtProvider, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * 토큰 검증 후 사용자 정보와 새 토큰을 반환합니다.
     * 매 호출마다 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     */
    public LoginResponse validateAndRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        log.info("사용자 인증 및 토큰 갱신: userId = {}, email = {}", user.getId(), user.getEmail());

        // JWT 액세스 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

        // 리프레시 토큰 생성 (기존 토큰 삭제 후 새로 생성)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(new LoginResponse.UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getNickname()
                ))
                .newUser(false) // 기존 사용자이므로 newUser는 false.
                .build();
    }
}
