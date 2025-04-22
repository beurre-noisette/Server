package hello.cokezet.temporary.domain.product.repository;

import hello.cokezet.temporary.domain.product.entity.Product;
import hello.cokezet.temporary.domain.product.repository.projection.ProductAndStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByStoreProductId(Long storeProductId);

	@Query("""
		SELECT new hello.cokezet.temporary.domain.product.repository.projection.ProductAndStore(
			p.id,
			p.storeProductId,
			p.price,
			p.pricePerMl,
			p.discountRate,
			p.size,
			p.brand,
			p.count,
			p.taste,
			s.id,
			s.name
		)
		FROM Product p
		JOIN Store s ON p.storeId = s.id
		JOIN StoreCardMapping scm ON s.id = scm.storeId
	""")
	Page<ProductAndStore> findAllAndCard(Pageable pageable);

	@Query("""
		SELECT new hello.cokezet.temporary.domain.product.repository.projection.ProductAndStore(
			p.id,
			p.storeProductId,
			p.price,
			p.pricePerMl,
			p.discountRate,
			p.size,
			p.brand,
			p.count,
			p.taste,
			s.id,
			s.name
		)
		FROM Product p
		JOIN Store s ON p.storeId = s.id
		WHERE p.id = :productId
	""")
	Optional<ProductAndStore> findAllAndCardProductId(@Param("productId") Long productId);
}
