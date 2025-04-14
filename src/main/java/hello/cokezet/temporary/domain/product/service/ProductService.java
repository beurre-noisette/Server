package hello.cokezet.temporary.domain.product.service;

import hello.cokezet.temporary.domain.product.entity.Product;
import hello.cokezet.temporary.domain.product.repository.ProductRepository;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import hello.cokezet.temporary.domain.store_card_mapping.entity.StoreCardMapping;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(
			ProductRepository productRepository
	) {
		this.productRepository = productRepository;
	}

	public List<GetProductResult> getProductList() {
		List<Product> productList = productRepository.findAllAndCard();

		return productList.stream()
				.map(product -> new GetProductResult(
						product,
						product.getStore()
								.getStoreCardMappingList().stream()
								.map(StoreCardMapping::getCard)
								.toList()
				))
				.toList();
	}
}