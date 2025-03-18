package hello.cokezet.temporary.domain.user.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.model.RefreshToken;
import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.SocialAccountRepository;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.domain.user.service.apple.AppleJwtKeyService;
import hello.cokezet.temporary.global.error.exception.InvalidSocialTokenException;
import hello.cokezet.temporary.global.model.Role;
import hello.cokezet.temporary.global.model.SocialProvider;
import hello.cokezet.temporary.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;

@Service
@Slf4j
public class AppleLoginService implements SocialLoginService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AppleJwtKeyService appleJwtKeyService;

    public AppleLoginService(UserRepository userRepository, SocialAccountRepository socialAccountRepository, JwtProvider jwtProvider, RefreshTokenService refreshTokenService, AppleJwtKeyService appleJwtKeyService) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.appleJwtKeyService = appleJwtKeyService;
    }

    @Value("${apple.client-id}")
    private String appleClientId;

    @Override
    public SocialProvider getSocialProvider() {
        return SocialProvider.APPLE;
    }

    @Override
    @Transactional
    public LoginResponse login(String idToken) {
        try {
            // Apple ID 토큰 파싱
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // JWT 헤더에서 kid(Key ID) 추출
            String kid = signedJWT.getHeader().getKeyID();

            // kid로 Apple 공개 키 조회
            JWK jwk = appleJwtKeyService.getPublicKey(kid);
            if (jwk == null) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 해당 공개 키를 찾을 수 없습니다");
            }

            // 토큰 검증
            boolean verified = signedJWT.verify(new ECDSAVerifier(jwk.toECKey()));
            if (!verified) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 서명 검증 실패");
            }

            // 토큰에서 클레임 추출
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // audience 검증 (클라이언트 ID와 일치해야 함)
            String aud = claimsSet.getAudience().getFirst();
            if (!appleClientId.equals(aud)) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 잘못된 audience");
            }

            // 토큰에서 사용자 정보 추출
            String providerId = claimsSet.getSubject(); // Apple의 사용자 ID
            String email = claimsSet.getStringClaim("email");

            // 소셜 계정 조회
            Optional<SocialAccount> socialAccountOptional = socialAccountRepository.findByProviderAndProviderId(
                    SocialProvider.APPLE, providerId
            );

            User user;
            boolean isNewUser = false;

            if (socialAccountOptional.isPresent()) {
                // 기존 소셜 계정이 있는 경우
                user = socialAccountOptional.get().getUser();
            } else {
                // 이메일로 기존 사용자 확인
                Optional<User> existingUser = userRepository.findByEmail(email);

                if (existingUser.isPresent()) {
                    // 동일 이메일의 사용자가 있는 경우, 소셜 계정 연결
                    user = existingUser.get();
                } else {
                    // 신규 사용자 생성
                    // 이름이 없는 경우 이메일의 첫 부분을 닉네임으로 사용
                    String name = email.split("@")[0];

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
                        .provider(SocialProvider.APPLE)
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
        } catch (ParseException | JOSEException e) {
            log.error("Apple ID 토큰 검증 실패", e);
            throw new InvalidSocialTokenException("유효하지 않은 Apple ID 토큰입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("소셜 로그인 처리 중 오류 발생", e);
            throw new RuntimeException("소셜 로그인 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}