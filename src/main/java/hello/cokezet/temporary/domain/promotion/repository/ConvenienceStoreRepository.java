package hello.cokezet.temporary.domain.promotion.repository;

import hello.cokezet.temporary.domain.promotion.entity.ConvenienceStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvenienceStoreRepository extends JpaRepository<ConvenienceStore, Long> {
    Optional<ConvenienceStore> findByName(String name);
}
