package hello.cokezet.temporary.domain.user.repository;

import hello.cokezet.temporary.domain.user.model.SocialAccount;
import hello.cokezet.temporary.global.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderId(SocialProvider provider, String providerId);

}
