package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.model.RefreshToken;
import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.SocialAccountRepository;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.global.model.Role;
import hello.cokezet.temporary.global.model.SocialProvider;
import hello.cokezet.temporary.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class GoogleLoginService implements SocialLoginService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenService refreshTokenService;

    public GoogleLoginService(UserRepository userRepository, SocialAccountRepository socialAccountRepository, JwtProvider jwtProvider, JwtDecoder jwtDecoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtProvider = jwtProvider;
        this.jwtDecoder = jwtDecoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public SocialProvider getSocialProvider() {
        return SocialProvider.GOOGLE;
    }

    @Override
    @Transactional
    public LoginResponse login(String idToken) {
        try {
            // Google ID 토큰 검증
            Jwt jwt = jwtDecoder.decode(idToken);

            // 토큰에서 사용자 정보 추출
            String providerId = jwt.getSubject(); // Google의 사용자 ID
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");

            // 소셜 계정 조회
            Optional<SocialAccount> socialAccountOptional = socialAccountRepository.findByProviderAndProviderId(
                    SocialProvider.GOOGLE, providerId
            );

            User user;
            boolean isNewUser = false;

            if (socialAccountOptional.isPresent()) {
                // 기존 소셜 계정이 있는 경우, 이름만 업데이트
                user = socialAccountOptional.get().getUser();
                user.updateProfile(name);
            } else {
                // 이메일로 기존 사용자 확인
                Optional<User> existingUser = userRepository.findByEmail(email);

                if (existingUser.isPresent()) {
                    // 동일 이메일의 사용자가 있는 경우, 소셜 계정 연결
                    user = existingUser.get();
                } else {
                    // 신규 사용자 생성
                    user = User.builder()
                            .email(email)
                            .nickname(name)
                            .role(Role.USER)
                            .build();

                    userRepository.save(user);
                    isNewUser = true;
                }

                // 소셜 계정 생성 및 연결
                SocialAccount socialAccount = SocialAccount.builder()
                        .user(user)
                        .provider(SocialProvider.GOOGLE)
                        .providerId(providerId)
                        .build();

                socialAccountRepository.save(socialAccount);
            }

            // JWT Access 토큰 생성
            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

            // 리프레시 토큰 생성 (계정당 하나의 기기만 허용)
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .user(new LoginResponse.UserInfo(
                            user.getId(),
                            user.getEmail(),
                            user.getNickname()
                    ))
                    .newUser(isNewUser)
                    .build();
        } catch (JwtValidationException e) {
            log.error("ID 토큰 검증 실패", e);
            throw new IllegalArgumentException("유효하지 않은 ID 토큰입니다.", e);
        } catch (Exception e) {
            log.error("소셜 로그인 처리 중 오류 발생", e);
            throw new RuntimeException("소셜 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }
}
