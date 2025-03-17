# CokeZet 제로콜라 최저가 알림 서비스

## 프로젝트 개요

CokeZet은 사용자가 선호하는 커머스에서 제로콜라 최저가 정보와 카드사 할인 혜택을 알려주는 모바일 서비스입니다. 매일 최저가를 찾아다니는 번거로움 없이, 원하는 조건의 할인 정보가 등록되면 알림을 받을 수 있습니다.

## 담당 영역

+ 소셜 로그인 시스템 : 플랫폼별 소셜 로그인 구현 (Android: Google/Kakao, iOS: Apple/Kakao)
+ 알림 기능 : 사용자 맞춤형 실시간 가격 알림 서비스

## 사용 기술 및 선택 이유

+ 언어 : Java 21
  
+ 프레임워크 : Spring Boot 3.4.3

+ ORM : Spring Data JPA/Hibernate
  + 테이블 중심의 모델링에 익숙해져 있었는데, 객체 중심적 아키텍쳐를 연습해보고자 JPA를 선택하였습니다.
  + JPA는 객체 간의 관계 표현과 도메인 모델링이 직관적이며, 반복적인 SQL 작성 없이 비즈니스 로직에 집중할 수 있다는 장점이 있다고 생각합니다.
  
+ 보안 : Spring Security, JWT
  + CokeZet는 네이티브 앱으로만 제공될 서비스입니다. 다음과 같은 이유로 세션 활용의 한계를 느꼈습니다.
    + 웹 브라우저와 달리 네이티브 앱은 쿠키를 자동으로 관리하지 않습니다. 따라서 세션을 사용할 경우 개발자가 직접 세션 ID 저장 및 요청 포함 로직을 구현해야 합니다.
    + 앱이 완전히 종료된 후에도 인증 상태를 유지해야 하는데, 이때 추가적인 로직이 필요합니다.
    + 추후 서비스의 규모가 커질 경우 세션 저장소 관리 부담이 증가할 것을 우려했습니다.
    + 모바일 네트워크 특성 상 불안정한 연결이나 높은 지연 시간 등의 문제가 있을 확률이 높다고 판단했으며, 이러한 모바일 네트워크 환경에서 세션 관리의 어려움이 우려됐습니다.
  + 따라서 다음과 같은 이유로 JWT를 세션의 대안으로 사용했습니다.
    + JWT의 Stateless 아키텍쳐 특성을 활요했습니다. 서버가 클라이언트의 상태를 저장할 필요가 없고 서버 측 세션 저장소가 불필요하며 토큰 자체에 저희가 요구할 최소한의 정보를 포함시킬 수 있다고 생각했습니다.
    + 추후 다중 서버 환경으로 확장을 고려했을 때 별도의 세션 공유 메커니즘 없이 인증을 할 수 있다고 판단했습니다.
    + **토큰을 저장소에 안전하게 보관하고 요청마다 헤더에 포함시키는 방식이 모바일 앱 아키텍쳐에 적합하다고 판단했습니다.**
  + `global/security/jwt/JwtProvider.java`, `global/security/jwt/JwtAuthenticationFilter.java`에 관련 코드가 있습니다.

+ 데이터베이스 : MySQL
  
+ API 문서화 : SpringDoc OpenAPI 2.8.5 (Swagger UI)
  + 앱 개발자와의 원활한 협업을 위해 표준화되고 자동화된 문서 생성이 필요하다고 느껴서 사용하였습니다.
  + 백엔드 개발자로써 코드 변경 시 문서가 자동으로 업데이트되어 항상 최신 상태를 유지할 수 있다는 점이 좋았습니다.
 
+ OAuth : Srping OAuth2 Client, Resource Server

+ JWT 관련 라이브러리 :
  + `io.jsonwebtoken:jjwt 0.11.5` : 기본인 JWT 생성 및 검증에 사용하였습니다.
  + `Nimbus JOSE+JWT 9.37.3(for Apple Login)` : Apple의 JWK 처리 및 ECDSA 서명 검증에 특화된 라이브러리여서 사용하였습니다.
  + `Auth0 JWT 4.4.0(for Apple Login)` : Apple Client Secret 생성 시 필요한 ECDSA 알고리즘을 지원하여 사용하였습니다.
  + `domain/user/service/apple/AppleJwtKeyService.java`, `domain/user/service/apple/AppleClientSecretService.java`에 관련 코드가 있습니다.

## 구현 기능 및 설계 결정 이유

### 소셜 로그인

#### 플랫폼별 최적화된 소셜 로그인

+ 각 플랫폼의 사용자 경험을 최적화하기 위해 플랫폼별 선호 로그인 방식을 구현하고자 했습니다.
  + Andorid : Google(기기 연동이 용이) + Kakao(국내 사용자 친화)
  + Apple : Apple(Apple 정책 준수) + Kakao(국내 사용자 친화)
+ 단일 백엔드에서 모든 인증 방식을 일관되게 처리하기 위한 아키텍쳐를 고민했습니다.
  + `domain/user/service/GoogleLoginService.java`, `domain/user/service/AppleLoginService.java`에 관련 코드가 있습니다.

#### `SocialLoginService` 인터페이스와 같은 전략 패턴을 적용하였습니다.

+ 각 소셜 로그인 제공자의 구현 세부사항을 캡슐화하여 유지보수성을 향상시키고자 하였습니다.
+ 새로운 소셜 로그인 추가 시 기존 코드 변경 없이 확장 가능한 설계를 해보고자 고민하였습니다.
+ `domain/user/service/SocialLoginService.java`, `domain/user/service/SocialLoginFactory.java`에 관련 코드가 있습니다.

#### Refresh Token Rotation 정책을 취했습니다.

+ 토큰이 탈취되어도 한 번 사용 후에는 무효화되어 보안 위험을 최소화할 수 있다고 판단했습니다.
+ 또한 토큰 재사용 감지 시 모든 리프레시 토큰을 무효화하는 방식으로 추가 보안 조치를 취했습니다.
+ `domain/user/service/RefreshTokenService.java`, `domain/user/model/RefreshToken.java`에 관련 코드가 있습니다.

#### 토큰 재사용 감지 로직을 구현했습니다.

+ 리프레쉬 토큰 탈취 시도를 실시간으로 감지하여 즉각적인 대응을 할 수 있도록 구현하였습니다.
+ 단순한 만료 처리가 아닌 사용 여부를 데이터베이스에 기록하여 추가적인 대응 여부를 결정할 수 있도록 처리하였습니다.
+ `global/error/exception/TokenReuseDetectedException.java`, `domain/user/service/RefreshTokenService.java`에 관련 코드가 있습니다.

## 기술적 도전과 해결 방법 및 이유

### 1. Apple 로그인 구현의 복잡성

문제 상황:

+ Apple의 OAuth 2.0 구현은 Google보다 훨씬 복잡한 검증 과정을 요구했습니다.
+ ID 토큰 검증을 위해 Apple의 공개키(JWK)를 가져와 ECDSA 서명을 검증해야 했습니다.
+ Apple 서버와의 통신 빈도가 높아질 경우 API 호출 제한에 도달할 위험이 있었습니다.
+ 키 관리와 토큰 검증 로직의 복잡도가 높았습니다.

해결 방법:

+ JWK 캐싱 메커니즘 구현: AppleJwtKeyService 클래스에서 Apple 공개키를 가져와 메모리에 캐싱하는 로직을 구현했습니다.

```java
// AppleJwtKeyService.java의 일부분
private void fetchAndCacheApplePublicKeys() {
    try {
        String response = restTemplate.getForObject(APPLE_PUBLIC_KEYS_URL, String.class);
        JWKSet jwkSet = JWKSet.parse(response);
        for (JWK jwk : jwkSet.getKeys()) {
            publicKeys.put(jwk.getKeyID(), jwk);
        }
        log.info("Apple 공개 키 캐싱 완료: {} 개의 키", publicKeys.size());
    } catch (Exception e) {
        log.error("Apple 공개 키 가져오기 실패", e);
        throw new RuntimeException("Apple 공개키를 가져오는 중 오류가 발생했습니다.", e);
    }
}
```

+ 토큰 검증 로직 최적화: Nimbus JOSE+JWT 라이브러리를 사용해 ID 토큰의 헤더에서 kid(Key ID)를 추출하고, 캐싱된 키를 사용해 서명을 검증하는 방식으로 구현했습니다.

```java
// AppleLoginService.java의 일부분
SignedJWT signedJWT = SignedJWT.parse(idToken);
String kid = signedJWT.getHeader().getKeyID();
JWK jwk = appleJwtKeyService.getPublicKey(kid);
boolean verified = signedJWT.verify(new ECDSAVerifier(jwk.toECKey()));
```

+ 예외 처리 강화: 토큰 검증 실패, 키 미존재 등 다양한 예외 상황에 대비한 세분화된 예외 처리를 구현했습니다.

성과:

+ Apple 서버 호출을 최소화하여 API 제한에 걸릴 위험을 낮추었습니다.
+ 토큰 검증 속도를 향상시켜 로그인 응답 시간을 단축했습니다.
+ 키 관리의 복잡성을 캡슐화하여 코드 가독성과 유지보수성을 향상시켰습니다.

### 2. JWT 토큰 보안 강화 과제

문제 상황:

+ JWT는 발급 후 만료되기 전까지 유효하여, 토큰이 탈취될 경우 보안 위험이 크다고 생각합니다.
+ Access Token의 유효 기간을 짧게 설정하면 사용자 경험이 저하되고, 길게 설정하면 보안 위험이 증가하는 딜레마가 있었습니다.
+ Refresh Token의 장기 보관에 따른 보안 위험도 고려해야 했습니다.

해결 방법:

+ Refresh Token Rotation 구현: 리프레시 토큰을 한 번만 사용할 수 있게 하고, 사용 시 새로운 리프레시 토큰을 발급하는 방식을 구현했습니다.

```java
// RefreshTokenService.java의 일부분
@Transactional
public RefreshTokenResponse refreshAccessToken(String token) {
    // 토큰 검증
    RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new RefreshTokenNotFoundException("존재하지 않는 Refresh Token 입니다."));

    // 사용 여부 확인 (재사용 감지)
    if (refreshToken.isUsed()) {
        // 보안 침해 가능성 : 모든 토큰 무효화
        refreshTokenRepository.deleteByUserId(refreshToken.getUserId());
        throw new TokenReuseDetectedException("토큰 재사용이 감지되었습니다. 보안을 위해 재로그인이 필요합니다.");
    }

    // 기존 토큰 사용 처리
    refreshToken.setUsed(true);
    refreshTokenRepository.save(refreshToken);

    // 새 리프레시 토큰 생성 (Rotation)
    RefreshToken newRefreshToken = createRefreshToken(refreshToken.getUserId());
    // 새 액세스 토큰 생성...
}
```

+ 토큰 재사용 감지 메커니즘: 이미 사용된 리프레시 토큰이 다시 사용되는 경우, 토큰 탈취 상황으로 판단하고 해당 사용자의 모든 리프레시 토큰을 무효화하는 로직을 구현했습니다.
Access Token과 Refresh Token의 유효 기간 최적화: Access Token은 30분으로 짧게, Refresh Token은 30일로 설정하여 보안과 사용성의 균형을 맞추었습니다.

```java
// application-jwt.yml
jwt:
  access-token-validity-in-seconds: 1800  # 30분
  refresh-token-validity-in-seconds: 2592000  # 30일
```

성과:

+ 토큰 탈취 상황에서도 공격자가 토큰을 재사용할 수 없게 하여 보안을 강화했습니다.
+ 토큰 재사용 감지 시 즉각적인 대응을 통해 추가 피해를 방지할 수 있게 되었습니다.
+ Access Token의 짧은 유효 기간으로 보안을 강화하면서도, Refresh Token Rotation을 통해 사용자 경험을 해치지 않는 방식을 구현했습니다.

### 3. 확장 가능한 아키텍처 설계 도전

문제 상황:

+ 여러 소셜 로그인 제공자(Google, Apple, Kakao)의 상이한 인증 방식을 일관되게 처리해야 했습니다.
+ 새로운 소셜 로그인이 추가되거나 기존 API가 변경될 때마다 전체 시스템에 영향을 주지 않는 구조가 필요했습니다.
+ 각 소셜 로그인의 세부 구현은 다르지만, 백엔드에서는 동일한 방식으로 인증 정보를 처리해야 했습니다.

해결 방법:

+ 전략 패턴과 팩토리 패턴 결합: `SocialLoginService` 인터페이스를 정의하고 각 소셜 로그인 제공자별로 구현체를 만들었으며, `SocialLoginFactory`를 통해 적절한 구현체를 선택하는 방식을 구현했습니다.

```java
// SocialLoginService.java
public interface SocialLoginService {
    LoginResponse login(String idToken);
    SocialProvider getSocialProvider();
}

// SocialLoginFactory.java의 일부분
@Component
@RequiredArgsConstructor
public class SocialLoginFactory {
    private final Set<SocialLoginService> loginServices;

    public SocialLoginService getLoginService(SocialProvider socialProvider) {
        return loginServices.stream()
                .filter(service -> service.getSocialProvider() == socialProvider)
                .findFirst()
                .orElseThrow(() -> new UnsupportedSocialTypeException(
                        "지원하지 않는 소셜 로그인입니다: " + socialProvider));
    }
}
```

+ 의존성 주입과 컴포넌트 스캔 활용: Spring의 의존성 주입 기능을 활용해 소셜 로그인 서비스를 자동으로 등록하고 필요한 곳에서 주입받아 사용하는 구조를 구현했습니다.
+ 추상화 계층 도입: 소셜 로그인의 세부 구현 차이를 추상화하여 컨트롤러 계층에서는 단일 인터페이스로 접근할 수 있게 했습니다.

```java
// AuthRestController.java의 일부분
@PostMapping("/login")
public ResponseEntity<ApiResult<LoginResponse>> socialLogin(@RequestBody SocialLoginRequest request) {
    SocialLoginService loginService = socialLoginFactory.getLoginService(request.getProvider());
    LoginResponse response = loginService.login(request.getIdToken());
    return ResponseEntity.ok(ApiResult.success(response));
}
```

성과:

+ 새로운 소셜 로그인 제공자 추가 시 기존 코드 변경 없이 새로운 구현체만 추가하면 되는 확장성을 확보했습니다.
+ 각 소셜 로그인의 API 변경 시 해당 구현체만 수정하면 되어 유지보수성이 향상되었습니다.
+ 컨트롤러와 서비스 계층의 결합도를 낮추어 테스트 용이성을 높였습니다.
+ Spring의 기능을 최대한 활용하여 구현 복잡도를 낮추고 코드 가독성을 높였습니다.

이러한 기술적 도전들을 해결하면서, 보안성과 확장성을 모두 고려한 견고한 백엔드 시스템을 구축하고자 노력하고 있습니다. 특히 모바일 앱 특성에 맞는 인증 시스템 구현과 사용자 경험을 해치지 않는 보안 강화 방안을 모색하는 과정이 가장 큰 학습 포인트였습니다.

## 향후 개발 계획과 이유

### FCM 푸시 알림 구현

+ 서비스의 핵심 가치인 "최저가 알림"을 제공하기 위해 필수적으로 구현해야 합니다.
+ 푸시 알림을 통해 사용자는 앱을 실행하지 않아도 중요 정보를 받아볼 수 있습니다.
