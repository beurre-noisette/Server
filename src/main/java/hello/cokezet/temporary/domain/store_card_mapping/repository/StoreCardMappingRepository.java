package hello.cokezet.temporary.domain.store_card_mapping.repository;

import hello.cokezet.temporary.domain.card.entity.Card;
import hello.cokezet.temporary.domain.store_card_mapping.entity.StoreCardMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreCardMappingRepository extends JpaRepository<StoreCardMapping, Long> {

	@Query("""
			SELECT c
			FROM StoreCardMapping scm
			join Card c on scm.cardId = c.id
			WHERE scm.storeId = :storeId
		""")
	List<Card> findByStoreId(Long storeId);

}
