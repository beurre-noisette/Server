package hello.cokezet.temporary.domain.user.service.apple;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AppleJwtKeyService {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final Map<String, JWK> publicKeys = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;

    public AppleJwtKeyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        fetchAndCacheApplePublicKeys();
    }

    /**
     * 주어진 kid에 해당하는 Apple 공개 키를 반환합니다.
     * 캐시에 없는 경우 Apple 서버에서 새로 가져옵니다.
     */
    public JWK getPublicKey(String kid) {
        if (!publicKeys.containsKey(kid)) {
            log.info("키 ID {}에 해당하는 키가 캐시에 없음, 새로 가져오기 시도", kid);
            fetchAndCacheApplePublicKeys();
        }

        JWK jwk = publicKeys.get(kid);
        if (jwk != null) {
            log.info("키 ID {}에 해당하는 키 조회 성공. 키 유형: {}", kid, jwk.getClass().getSimpleName());
        } else {
            log.warn("키 ID {}에 해당하는 키를 찾을 수 없음", kid);
        }

        return jwk;
    }

    /**
     * Apple 서버에서 공개 키를 가져와 캐시에 저장합니다.
     */
    private void fetchAndCacheApplePublicKeys() {
        try {
            String response = restTemplate.getForObject(APPLE_PUBLIC_KEYS_URL, String.class);

            // null 체크 추가
            if (response == null) {
                throw new RuntimeException("Apple 공개키 응답이 null입니다.");
            }

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
}
