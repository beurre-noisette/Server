package hello.cokezet.temporary.domain.user.repository;

import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.domain.user.model.User;
import hello.cokezet.temporary.global.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderId(SocialProvider provider, String providerId);

    // [테스트용] 특정 소셜 제공자를 사용하는 모든 소셜 계정 조회
    List<SocialAccount> findByProvider(SocialProvider provider);

    /**
     * 사용자와 소셜 제공자로 소셜 계정 조회
     * @param user 사용자 엔티티
     * @param provider 소셜 로그인 제공자
     * @return 소셜 계정 정보
     */
    Optional<SocialAccount> findByUserAndProvider(User user, SocialProvider provider);

    List<SocialAccount> findByUser(User user);
}
