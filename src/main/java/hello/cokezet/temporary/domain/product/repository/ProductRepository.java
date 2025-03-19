package hello.cokezet.temporary.domain.product.repository;

import hello.cokezet.temporary.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	public boolean existsByStoreProductId(Long storeProductId);
}
