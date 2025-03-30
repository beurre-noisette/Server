package hello.cokezet.temporary.domain.user.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    // 테스트할 서비스 (의존성 주입)
    @InjectMocks
    private UserProfileService userProfileService;

    // 모의 객체(Mock)로 대체할 의존성들
    @Mock
    private UserRepository userRepository;

    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserDeletionLogRepository userDeletionLogRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppleClientSecretService appleClientSecretService;

    // 테스트에 사용할 더미 객체들
    private User testUser;
    private SocialAccount testSocialAccount;

    @BeforeEach
    void setUp() {
        // @Value 어노테이션으로 주입되는 값을 직접 설정
        ReflectionTestUtils.setField(userProfileService, "appleClientId", "test.client.id");

        // 테스트 데이터 준비
        testUser = mock(User.class);
        // lenient() 추가하여 엄격한 검증 완화
        lenient().when(testUser.getId()).thenReturn(1L);
        lenient().when(testUser.getEmail()).thenReturn("test@example.com");
        lenient().when(testUser.getNickname()).thenReturn("테스트사용자");

        testSocialAccount = mock(SocialAccount.class);
        lenient().when(testSocialAccount.getProvider()).thenReturn(SocialProvider.GOOGLE);
        lenient().when(testSocialAccount.getProviderId()).thenReturn("google-user-123");
    }

    @Test
    @DisplayName("구글 계정 회원탈퇴 성공 케이스")
    void deleteUserAccount_WithGoogleAccount_ShouldSucceed() {
        // 1. Given (테스트 환경 설정)
        Long userId = 1L;
        String idToken = "test-google-token";

        // Mock 객체의 동작 정의
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(socialAccountRepository.findByUserAndProvider(testUser, SocialProvider.GOOGLE))
                .thenReturn(Optional.of(testSocialAccount));

        // RestTemplate의 exchange 메서드가 호출되면 성공 응답 반환하도록 설정
        ResponseEntity<String> mockResponse = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.exchange(
                contains("https://accounts.google.com/o/oauth2/revoke"),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        )).thenReturn(mockResponse);

        // 2. When (테스트 대상 메서드 실행)
        userProfileService.deleteUserAccount(userId, SocialProvider.GOOGLE, idToken);

        // 3. Then (결과 검증)
        // 사용자 소프트 삭제 확인
        verify(testUser).softDelete();
        // ManyToMany 관계 정리 확인
        verify(testUser).setPreferredCommerces(any());
        verify(testUser).setPreferredCardCompanies(any());
        // 사용자 저장 확인
        verify(userRepository).save(testUser);
        // 리프레시 토큰 삭제 확인
        verify(refreshTokenRepository).deleteByUserId(userId);
        // 삭제 로그 저장 확인
        verify(userDeletionLogRepository).save(any(UserDeletionLog.class));
        // 소셜 연결 해제 API 호출 확인
        verify(restTemplate).exchange(
                contains("https://accounts.google.com/o/oauth2/revoke"),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        );
    }

    @Test
    @DisplayName("애플 계정 회원탈퇴 성공 케이스")
    void deleteUserAccount_WithAppleAccount_ShouldSucceed() throws Exception {
        // 1. Given
        Long userId = 1L;
        String idToken = "test-apple-token";

        // Mock 설정
        when(testSocialAccount.getProvider()).thenReturn(SocialProvider.APPLE);
        when(testSocialAccount.getProviderId()).thenReturn("apple-user-456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(socialAccountRepository.findByUserAndProvider(testUser, SocialProvider.APPLE))
                .thenReturn(Optional.of(testSocialAccount));

        // 애플 클라이언트 시크릿 생성 모킹
        when(appleClientSecretService.generateClientSecret()).thenReturn("mock-apple-secret");

        // 애플 API 호출 모킹
        ResponseEntity<String> mockResponse = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://appleid.apple.com/auth/revoke"),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        )).thenReturn(mockResponse);

        // 2. When
        userProfileService.deleteUserAccount(userId, SocialProvider.APPLE, idToken);

        // 3. Then
        verify(testUser).softDelete();
        verify(appleClientSecretService).generateClientSecret();
        verify(restTemplate).exchange(
                eq("https://appleid.apple.com/auth/revoke"),
                eq(HttpMethod.POST),
                any(),
                eq(String.class)
        );
    }

    @Test
    @DisplayName("사용자를 찾을 수 없는 경우")
    void deleteUserAccount_UserNotFound_ShouldThrowException() {
        // Given
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userProfileService.deleteUserAccount(nonExistentUserId, SocialProvider.GOOGLE, "any-token");
        });
    }

    @Test
    @DisplayName("소셜 연결 해제 실패 시")
    void deleteUserAccount_SocialRevokeFailed_ShouldThrowException() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(socialAccountRepository.findByUserAndProvider(testUser, SocialProvider.GOOGLE))
                .thenReturn(Optional.of(testSocialAccount));

        // RestTemplate이 예외를 던지도록 설정
        when(restTemplate.exchange(
                contains("https://accounts.google.com/o/oauth2/revoke"),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        )).thenThrow(new RuntimeException("API 호출 실패"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userProfileService.deleteUserAccount(userId, SocialProvider.GOOGLE, "test-token");
        });

        assertEquals(ErrorCode.SOCIAL_REVOKE_FAILED, exception.getErrorCode());
    }
}
