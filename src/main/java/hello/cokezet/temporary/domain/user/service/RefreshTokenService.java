package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.dto.response.RefreshTokenResponse;
import hello.cokezet.temporary.domain.user.model.RefreshToken;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.RefreshTokenRepository;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.global.error.exception.ExpiredTokenException;
import hello.cokezet.temporary.global.error.exception.RefreshTokenNotFoundException;
import hello.cokezet.temporary.global.error.exception.TokenReuseDetectedException;
import hello.cokezet.temporary.global.error.exception.UserNotFoundException;
import hello.cokezet.temporary.global.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProvider jwtProvider, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // 기존 토큰이 있으면 삭제 (한 계정당 하나의 로그인 유지)
        refreshTokenRepository.deleteByUserId(userId);
        
        // 새 리프레시 토큰 생성
        String token = jwtProvider.generateRefreshToken();
        Date expiration = jwtProvider.getExpirationDateFromToken(token);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiryDate(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .used(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // 토큰 검증 및 갱신 (Rotation 포함)
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

        // 만료 확인
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredTokenException("만료된 리프레시 토큰입니다.");
        }

        // 기존 토큰 사용 처리
        refreshToken.setUsed(true);
        refreshTokenRepository.save(refreshToken);

        // 새 리프레시 토큰 생성 (Rotation)
        RefreshToken newRefreshToken = createRefreshToken(refreshToken.getUserId());

        // 새 액세스 토큰 생성을 위한 사용자 정보 조회
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    /**
     * 임시 메서드
     * 사용자 로그아웃 - 리프레시 토큰 삭제
     */
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("userId={}의 리프레시 토큰 삭제 완료", userId);
    }
}
