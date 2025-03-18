package hello.cokezet.temporary.domain.Product.repository;

import hello.cokezet.temporary.domain.Product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	public boolean existsByStoreProductId(Long storeProductId);
}
