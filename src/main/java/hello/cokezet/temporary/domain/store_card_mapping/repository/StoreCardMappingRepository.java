package hello.cokezet.temporary.domain.store_card_mapping.repository;

import hello.cokezet.temporary.domain.store_card_mapping.entity.StoreCardMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCardMappingRepository extends JpaRepository<StoreCardMapping, Long> {
}
