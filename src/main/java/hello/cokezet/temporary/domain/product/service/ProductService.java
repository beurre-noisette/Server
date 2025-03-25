package hello.cokezet.temporary.domain.product.service;

import hello.cokezet.temporary.domain.product.entity.Product;
import hello.cokezet.temporary.domain.product.repository.ProductRepository;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import hello.cokezet.temporary.domain.store_card_mapping.entity.StoreCardMapping;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(
			ProductRepository productRepository
	) {
		this.productRepository = productRepository;
	}

	public List<GetProductResult> getProductList() {
		List<Product> productList = productRepository.findAll();

		return productList.stream()
				.filter(product -> !Objects.equals(product.getBrand(), "펩시") && !Objects.equals(product.getTaste(), "original"))
				.map(product -> {
					return new GetProductResult(
							product,
							product.getStore()
									.getStoreCardMappingList().stream()
									.map(StoreCardMapping::getCard)
									.toList()
					);
				})
				.toList();
	}
}