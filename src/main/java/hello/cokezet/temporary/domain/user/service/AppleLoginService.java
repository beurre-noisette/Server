package hello.cokezet.temporary.domain.user.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
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
import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
public class AppleLoginService implements SocialLoginService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AppleJwtKeyService appleJwtKeyService;

    public AppleLoginService(UserRepository userRepository, SocialAccountRepository socialAccountRepository,
                             JwtProvider jwtProvider, RefreshTokenService refreshTokenService,
                             AppleJwtKeyService appleJwtKeyService) {
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
    public LoginResponse login(String encodedIdToken) {
        try {
            // 1. Base64로 인코딩된 토큰을 디코딩
            String idToken = new String(Base64.getDecoder().decode(encodedIdToken));
            log.info("Base64 디코딩된 JWT: {}", idToken);

            // 2. Apple ID 토큰 파싱 및 검증
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            String kid = signedJWT.getHeader().getKeyID();
            log.info("키 ID(kid): {}", kid);

            // 3. 토큰의 audience 확인
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String aud = claimsSet.getAudience().getFirst();

            // 클라이언트 ID와 일치하는지 확인
            if (!appleClientId.equals(aud)) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 잘못된 audience");
            }

            log.info("토큰 audience 검증 성공: {}", aud);

            // 4. kid로 Apple 공개 키 조회
            JWK jwk = appleJwtKeyService.getPublicKey(kid);
            if (jwk == null) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 해당 공개 키를 찾을 수 없습니다");
            }

            // 5. 토큰 서명 검증
            boolean verified = verifyTokenSignature(signedJWT, jwk);
            if (!verified) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 서명 검증 실패");
            }

            // 6. 토큰에서 사용자 정보 추출
            String providerId = claimsSet.getSubject(); // Apple의 사용자 ID
            String email = claimsSet.getStringClaim("email");

            if (email == null || email.isBlank()) {
                throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: 이메일 정보가 없습니다");
            }

            // 7. 사용자 정보 처리 및 로그인 응답 생성
            return processUserAndGenerateResponse(providerId, email);

        } catch (ParseException e) {
            log.error("Apple ID 토큰 파싱 실패", e);
            throw new InvalidSocialTokenException("유효하지 않은 Apple ID 토큰입니다: " + e.getMessage());
        } catch (InvalidSocialTokenException e) {
            // 이미 적절한 메시지가 포함된 예외이므로 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("Apple 로그인 처리 중 오류 발생", e);
            throw new RuntimeException("소셜 로그인 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 토큰 서명을 검증합니다.
     */
    private boolean verifyTokenSignature(SignedJWT signedJWT, JWK jwk) throws InvalidSocialTokenException {
        try {
            if (jwk instanceof RSAKey rsaKey) {
                return signedJWT.verify(new RSASSAVerifier(rsaKey.toRSAPublicKey()));
            } else if (jwk instanceof ECKey ecKey) {
                return signedJWT.verify(new ECDSAVerifier(ecKey.toECPublicKey()));
            } else {
                throw new InvalidSocialTokenException("지원하지 않는 키 유형입니다: " + jwk.getClass().getSimpleName());
            }
        } catch (JOSEException e) {
            log.error("서명 검증 중 예외 발생", e);
            throw new InvalidSocialTokenException("유효하지 않은 Apple 토큰입니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 정보를 처리하고 로그인 응답을 생성합니다.
     */
    private LoginResponse processUserAndGenerateResponse(String providerId, String email) {
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
                String nickname = email.split("@")[0];

                user = User.builder()
                        .email(email)
                        .nickname(nickname)
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
    }
}