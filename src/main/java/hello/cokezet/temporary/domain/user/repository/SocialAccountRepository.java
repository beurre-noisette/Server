package hello.cokezet.temporary.domain.user.repository;

import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.global.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderId(SocialProvider provider, String providerId);

    // [테스트용] 특정 소셜 제공자를 사용하는 모든 소셜 계정 조회
    List<SocialAccount> findByProvider(SocialProvider provider);
}
