package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.request.SocialRevokeRequest;
import hello.cokezet.temporary.domain.user.dto.response.LoginResponse;
import hello.cokezet.temporary.domain.user.model.RefreshToken;
import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.SocialAccountRepository;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.global.error.ErrorCode;
import hello.cokezet.temporary.global.error.exception.BusinessException;
import hello.cokezet.temporary.global.model.Role;
import hello.cokezet.temporary.global.model.SocialProvider;
import hello.cokezet.temporary.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class GoogleLoginService implements SocialDisconnectService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenService refreshTokenService;
    private final RestTemplate restTemplate;

    private static final String GOOGLE_REVOKE_URL = "https://oauth2.googleapis.com/revoke";

    public GoogleLoginService(UserRepository userRepository, SocialAccountRepository socialAccountRepository, 
                             JwtProvider jwtProvider, JwtDecoder jwtDecoder, 
                             RefreshTokenService refreshTokenService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtProvider = jwtProvider;
        this.jwtDecoder = jwtDecoder;
        this.refreshTokenService = refreshTokenService;
        this.restTemplate = restTemplate;
    }

    @Override
    public SocialProvider getSocialProvider() {
        return SocialProvider.GOOGLE;
    }

    /**
     * Google 계정과의 연결을 해제합니다.
     * Google OAuth 토큰 취소 API를 호출하여 사용자의 액세스 토큰을 무효화합니다.
     *
     * @param socialAccount 연결 해제할 소셜 계정 정보
     * @param request 연결 해제에 필요한 요청 정보 (액세스 토큰)
     * @return 연결 해제 성공 여부
     */
    @Override
    public boolean revoke(SocialAccount socialAccount, SocialRevokeRequest request) {
        if (socialAccount.getProvider() != SocialProvider.GOOGLE) {
            log.error("잘못된 소셜 제공자: 예상={}, 실제={}", SocialProvider.GOOGLE, socialAccount.getProvider());
            throw new BusinessException(ErrorCode.SOCIAL_REVOKE_FAILED, "Google 계정만 해제할 수 있습니다.");
        }

        try {
            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 요청 파라미터 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("token", request.getRevokeToken());

            // HTTP 요청 생성 및 전송
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(GOOGLE_REVOKE_URL, requestEntity, Void.class);

            // 응답 확인
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Google 계정 연결 해제 성공: providerId={}", socialAccount.getProviderId());
                return true;
            } else {
                log.error("Google 계정 연결 해제 실패: providerId={}, statusCode={}", 
                        socialAccount.getProviderId(), response.getStatusCode());
                return false;
            }
        } catch (RestClientException e) {
            log.error("Google 계정 연결 해제 중 오류 발생: providerId={}, error={}", 
                    socialAccount.getProviderId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_REVOKE_FAILED, 
                    "Google 계정 연결 해제 중 오류가 발생했습니다: " + e.getMessage());
        }
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
