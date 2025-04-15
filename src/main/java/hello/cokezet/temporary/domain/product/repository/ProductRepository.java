package hello.cokezet.temporary.domain.product.repository;

import hello.cokezet.temporary.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByStoreProductId(Long storeProductId);

	@Query("""
			SELECT p
			FROM Product p
			JOIN FETCH p.store s
			JOIN FETCH s.storeCardMappingList scm
			JOIN FETCH scm.card c
		""")
	List<Product> findAllAndCard();
}
