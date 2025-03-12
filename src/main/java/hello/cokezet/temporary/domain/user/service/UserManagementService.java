package hello.cokezet.temporary.domain.user.service;

import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.domain.user.repository.RefreshTokenRepository;
import hello.cokezet.temporary.domain.user.repository.SocialAccountRepository;
import hello.cokezet.temporary.domain.user.repository.UserRepository;
import hello.cokezet.temporary.global.error.exception.UserNotFoundException;
import hello.cokezet.temporary.global.model.SocialProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("!prod") // 운영 환경에서는 사용 불가
public class UserManagementService {

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 특정 소셜 제공자로 로그인한 모든 사용자의 토큰을 삭제합니다.
     */
    @Transactional
    public void logoutUsersByProvider(SocialProvider provider) {
        // 특정 소셜 제공자를 사용하는 모든 소셜 계정 조회
        List<SocialAccount> accounts = socialAccountRepository.findByProvider(provider);

        // 해당 소셜 계정을 사용하는 사용자 ID 목록 추출
        Set<Long> userIds = accounts.stream()
                .map(account -> account.getUser().getId())
                .collect(Collectors.toSet());

        // 해당 사용자들의 RefreshToken 삭제
        for (Long userId : userIds) {
            refreshTokenRepository.deleteByUserId(userId);
        }

        log.info("{}개의 {} 사용자 로그아웃 처리 완료", userIds.size(), provider);
    }

    /**
     * 특정 소셜 제공자로 가입한 모든 사용자와 관련 데이터를 삭제합니다.
     * @return 삭제된 사용자 수
     */
    @Transactional
    public int deleteUsersByProvider(SocialProvider provider) {
        // 특정 소셜 제공자를 사용하는 모든 소셜 계정 조회
        List<SocialAccount> accounts = socialAccountRepository.findByProvider(provider);

        // 해당 소셜 계정을 사용하는 사용자 ID 목록 추출
        Set<Long> userIds = accounts.stream()
                .map(account -> account.getUser().getId())
                .collect(Collectors.toSet());

        int count = userIds.size();

        // 해당 사용자들의 RefreshToken 삭제
        for (Long userId : userIds) {
            refreshTokenRepository.deleteByUserId(userId);
        }

        // 사용자 삭제 (소셜 계정은 CASCADE로 자동 삭제됨)
        for (Long userId : userIds) {
            userRepository.deleteById(userId);
        }

        log.info("{}개의 {} 사용자 삭제 완료", count, provider);
        return count;
    }

    /**
     * 특정 이메일의 사용자와 관련 데이터를 삭제합니다.
     */
    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: " + email));

        // RefreshToken 삭제
        refreshTokenRepository.deleteByUserId(user.getId());

        // 사용자 삭제 (소셜 계정은 CASCADE로 자동 삭제됨)
        userRepository.delete(user);

        log.info("이메일 {}의 사용자 삭제 완료", email);
    }
}