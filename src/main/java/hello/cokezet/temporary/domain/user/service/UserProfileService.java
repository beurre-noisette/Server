package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.benefit.model.CardCompany;
import hello.cokezet.temporary.domain.benefit.model.Commerce;
import hello.cokezet.temporary.domain.benefit.repository.CardCompanyRepository;
import hello.cokezet.temporary.domain.benefit.repository.CommerceRepository;
import hello.cokezet.temporary.domain.user.dto.request.ProfileUpdateRequest;
import hello.cokezet.temporary.domain.user.dto.response.ProfileResponse;
import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.model.UserDeletionLog;
import hello.cokezet.temporary.domain.user.repository.RefreshTokenRepository;
import hello.cokezet.temporary.domain.user.repository.SocialAccountRepository;
import hello.cokezet.temporary.domain.user.repository.UserDeletionLogRepository;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.domain.user.service.apple.AppleClientSecretService;
import hello.cokezet.temporary.global.error.ErrorCode;
import hello.cokezet.temporary.global.error.exception.BusinessException;
import hello.cokezet.temporary.global.error.exception.UserNotFoundException;
import hello.cokezet.temporary.global.model.SocialProvider;
import hello.cokezet.temporary.global.security.jwt.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;
    private final CommerceRepository commerceRepository;
    private final CardCompanyRepository cardCompanyRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDeletionLogRepository userDeletionLogRepository;
    private final RestTemplate restTemplate;
    private final AppleClientSecretService appleClientSecretService;

    @Value("${apple.client-id}")
    private String appleClientId;

    public UserProfileService(UserRepository userRepository, CommerceRepository commerceRepository, CardCompanyRepository cardCompanyRepository, SocialAccountRepository socialAccountRepository, RefreshTokenRepository refreshTokenRepository, UserDeletionLogRepository userDeletionLogRepository, RestTemplate restTemplate, AppleClientSecretService appleClientSecretService) {
        this.userRepository = userRepository;
        this.commerceRepository = commerceRepository;
        this.cardCompanyRepository = cardCompanyRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDeletionLogRepository = userDeletionLogRepository;
        this.restTemplate = restTemplate;
        this.appleClientSecretService = appleClientSecretService;
    }

    @Transactional
    public void updateUserProfile(UserPrincipal principal, ProfileUpdateRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        try {
            // 닉네임 업데이트
            if (request.getNickname() != null) {
                user.updateProfile(request.getNickname());
            }

            // 최초 설정 케이스 확인 (프로필이 미완성인 경우)
            boolean isInitialSetup = !user.isProfileComplete();

            // 선호 커머스 설정
            if (request.getCommerceIds() != null) {
                // 최초 설정인 경우 빈 값 체크
                if (isInitialSetup && request.getCommerceIds().isEmpty()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "선호 커머스는 최소 1개 이상 선택해야 합니다.");
                }

                // ID 유효성 검증
                Set<Commerce> commerces = new HashSet<>(commerceRepository.findAllById(request.getCommerceIds()));
                if (commerces.size() != request.getCommerceIds().size()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청한 커머스 ID 중 일부가 존재하지 않습니다.");
                }

                // 기존 값을 새 값으로 대체 (User 클래스의 setPreferredCommerces 메서드가 clear 후 addAll)
                user.setPreferredCommerces(commerces);
            }

            // 선호 카드사 설정
            if (request.getCardCompanyIds() != null) {
                // 최초 설정인 경우 빈 값 체크
                if (isInitialSetup && request.getCardCompanyIds().isEmpty()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "선호 카드사는 최소 1개 이상 선택해야 합니다.");
                }

                // ID 유효성 검증
                Set<CardCompany> cardCompanies = new HashSet<>(cardCompanyRepository.findAllById(request.getCardCompanyIds()));
                if (cardCompanies.size() != request.getCardCompanyIds().size()) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청한 카드사 ID 중 일부가 존재하지 않습니다.");
                }

                // 기존 값을 새 값으로 대체
                user.setPreferredCardCompanies(cardCompanies);
            }

            // 최초 설정인 경우, 두 항목 모두 있는지 확인
            if (isInitialSetup && (request.getCommerceIds() == null || request.getCardCompanyIds() == null)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최초 프로필 설정 시 선호 커머스와 카드사 모두 설정해야 합니다.");
            }

            userRepository.save(user);
            log.info("사용자 프로필 업데이트 완료 = {}", user.getId());

        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.PROFILE_UPDATE_FAILED, "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 선호 커머스 정보 변환
        Set<ProfileResponse.CommerceInfo> commerceInfos = user.getPreferredCommerces().stream()
                .map(commerce -> ProfileResponse.CommerceInfo.builder()
                        .id(commerce.getId())
                        .name(commerce.getName())
                        .build())
                .collect(Collectors.toSet());

        // 선호 카드사 정보 변환
        Set<ProfileResponse.CardCompanyInfo> cardCompanyInfos = user.getPreferredCardCompanies().stream()
                .map(cardCompany -> ProfileResponse.CardCompanyInfo.builder()
                        .id(cardCompany.getId())
                        .name(cardCompany.getName())
                        .build())
                .collect(Collectors.toSet());

        // 프로필 응답 생성
        return ProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileComplete(user.isProfileComplete())
                .preferredCommerces(commerceInfos)
                .preferredCardCompanies(cardCompanyInfos)
                .build();
    }

    // 사용자 프로필 완성 여부 확인
    public boolean isProfileComplete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return user.isProfileComplete();
    }

    /**
     * 회원 탈퇴 처리
     * 사용자 정보를 소트프 삭제하고 소셜 플랫폼과의 연결을 해제합니다
     *
     * @param userId         탈퇴할 사용자 ID
     * @param socialProvider 소셜 로그인 제공자
     * @param idToken    소셜 플랫폼 토큰
     */
    @Transactional
    public void deleteUserAccount(Long userId, SocialProvider socialProvider, String idToken) {
        try {
            // 1. 사용자 조회
            User user = userRepository.findById(userId).
                    orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

            // 2. 소셜 계정 정보 조회
            SocialAccount socialAccount = socialAccountRepository.findByUserAndProvider(user, socialProvider)
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                            "해당 소셜 계정으로 연결된 계정이 없습니다."));

            // 3. 소셜 연결 해제
            revokeSocialConnection(socialProvider, idToken, socialAccount.getProviderId());

            // 4. 탈퇴 로그 저장
            saveUserDeletionLog(user, socialAccount);

            // 5. 리프레쉬 토큰 삭제
            refreshTokenRepository.deleteByUserId(userId);

            // 6. 사용자 데이터 처리 (소프트 삭제 및 관계 정리)
            processDeletion(user);

            log.info("사용자 ID {} 탈퇴 처리 완료", userId);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("회원탈퇴 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.ACCOUNT_DELETE_FAILED, "회원탈퇴 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 소셜 플랫폼과의 연결 해제
     * 각 소셜 플랫폼의 API를 호출하여 사용자의 연결을 해제한다.
     *
     * @param socialProvider 소셜 로그인 제공자
     * @param idToken 소셜 플랫폼 토큰
     * @param providerId 소셜 플랫폼 사용자 ID
     */
    private void revokeSocialConnection(SocialProvider socialProvider, String idToken, String providerId) {
        try {
            switch (socialProvider) {
                case GOOGLE -> revokeGoogleConnection(idToken);
                case APPLE -> revokeAppleConnection(idToken, providerId);
                default -> throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_TYPE, "지원하지 않는 소셜 로그인 유형입니다.");
            }
            log.info("소셜 로그인 연결 해제 성공: socialProvider = {}, providerId = {}", socialProvider, providerId);
        } catch (Exception e) {
            log.error("소셜 연결 해제 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_REVOKE_FAILED, "소셜 연결 해제에 실패했습니다." + e.getMessage(), e);
        }
    }

    /**
     * 구글 계정 연결 해제
     * 구글의 토큰 취소 API를 호출한다.
     * 
     * @param idToken 구글 액세스 토큰
     */
    private void revokeGoogleConnection(String idToken) {
        final String revokeUrl = "https://accounts.google.com/o/oauth2/revoke?token=" + idToken;

        try {
            restTemplate.exchange(revokeUrl, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SOCIAL_REVOKE_FAILED, "구글 연결 해제에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 애플 계정 연결 해제
     * 애플의 토큰 취소 API를 호출한다
     *
     * @param idToken 애플 토큰
     * @param providerId 애플 사용자 ID
     */
    private void revokeAppleConnection(String idToken, String providerId) {
        try {
            // 애플 클라이언트 시크릿 생성
            String clientSecret = appleClientSecretService.generateClientSecret();

            // 애플 토큰 취소 요청
            final String revokeUrl = "https://appleid.apple.com/auth/revoke";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", appleClientId);
            body.add("client_secret", clientSecret);
            body.add("token", idToken);
            body.add("token_type_hint", "access_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.exchange(revokeUrl, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SOCIAL_REVOKE_FAILED,
                    "애플 연결 해제에 실패했습니다.: " + e.getMessage(), e);
        }
    }

    /**
     * 사용자 탈퇴 로그 저장
     * 탈퇴한 사용자의 정보를 로그 테이블에 기록한다
     *
     * @param user 탈퇴할 사용자
     * @param socialAccount 소셜 계정 정보
     */
    private void saveUserDeletionLog(User user, SocialAccount socialAccount) {
        UserDeletionLog userDeletionLog = UserDeletionLog.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .socialProvider(socialAccount.getProvider())
                .socialProviderId(socialAccount.getProviderId())
                .deletedAt(LocalDateTime.now())
                .build();

        userDeletionLogRepository.save(userDeletionLog);
    }

    /**
     * 사용자 데이터 처리 (소프트 삭제 및 관계 정리)
     * ManyToMany 관계를 정리하고 사용자를 소프트 삭제한다
     *
     * @param user 탈퇴할 사용자
     */
    private void processDeletion(User user) {
        // 1. ManyToMany 관계 정리
        user.setPreferredCommerces(new HashSet<>());
        user.setPreferredCardCompanies(new HashSet<>());

        // 2. 소프트 삭제 처리
        user.softDelete();

        // 3. 저장
        userRepository.save(user);
    }
}
