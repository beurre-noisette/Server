package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.request.SocialRevokeRequest;
import hello.cokezet.temporary.domain.user.model.SocialAccount;

/**
 * 소셜 플랫폼과의 연결 해제를 처리하는 인터페이스
 * SocialLoginService를 확장하여 연결 해제 기능을 추가
 */
public interface SocialDisconnectService extends SocialLoginService {
    
    /**
     * 소셜 플랫폼과의 연결을 해제합니다.
     * 
     * @param socialAccount 연결 해제할 소셜 계정 정보
     * @param request 연결 해제에 필요한 요청 정보 (토큰 등)
     * @return 연결 해제 성공 여부
     */
    boolean revoke(SocialAccount socialAccount, SocialRevokeRequest request);
}