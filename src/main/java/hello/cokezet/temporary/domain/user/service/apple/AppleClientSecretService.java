package hello.cokezet.temporary.domain.user.service.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class AppleClientSecretService {

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.private-key}")
    private String privateKeyContent;

    private ECPrivateKey getPrivateKey() throws Exception {
        String privateKey = privateKeyContent.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    // 백엔드와 Apple 서버 간 인증에 사용되는 JWT, 향후 Authorization code 방식을 사용하거나 추가 Apple API를 호출할 때 사용될 수 있음
    public String generateClientSecret() throws Exception {
        try {
            // 유효기간 6개월 짜리 JWT 생성
            Instant now = Instant.now();

            return JWT.create()
                    .withIssuer(teamId)
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusSeconds(15_777_000))) // 6개월, Apple의 요청사항
                    .withAudience("https://appleid.apple.com")
                    .withSubject(clientId)
                    .withKeyId(keyId)
                    .sign(Algorithm.ECDSA256(getPrivateKey()));
        } catch (Exception e) {
            throw new Exception("Client Secret 생성 실패: " + e.getMessage(), e);
        }
    }

}
