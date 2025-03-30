package hello.cokezet.temporary.domain.content.repository;

import hello.cokezet.temporary.domain.content.model.Policy;
import hello.cokezet.temporary.domain.content.model.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByType(PolicyType type);
}
