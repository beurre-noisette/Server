package hello.cokezet.temporary.domain.product.service;

import hello.cokezet.temporary.domain.card.entity.Card;
import hello.cokezet.temporary.domain.product.controller.dto.request.GetProductListRequest;
import hello.cokezet.temporary.domain.product.repository.ProductRepository;
import hello.cokezet.temporary.domain.product.repository.projection.ProductAndStore;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import hello.cokezet.temporary.domain.product.service.data.ProductAndStoreAndCardAndUrl;
import hello.cokezet.temporary.domain.store_card_mapping.repository.StoreCardMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final StoreCardMappingRepository storeCardMappingRepository;

	public ProductService(
			ProductRepository productRepository,
			StoreCardMappingRepository storeCardMappingRepository
	) {
		this.productRepository = productRepository;
		this.storeCardMappingRepository = storeCardMappingRepository;
	}

	public List<GetProductResult> getProductList(GetProductListRequest request) {
		List<ProductAndStore> productAndStoreList = productRepository.findAllAndCard(request.getPageable())
				.toList();

		return productAndStoreList.stream()
				.map(productAndStore -> {
					List<Card> cardList = storeCardMappingRepository.findByStoreId(productAndStore.storeId());

					return new GetProductResult(
							productAndStore.productId(),
							productAndStore.storeProductId(),
							productAndStore.price(),
							productAndStore.pricePerMl(),
							productAndStore.discountRate(),
							productAndStore.size(),
							productAndStore.brand(),
							productAndStore.count(),
							productAndStore.taste(),
							productAndStore.storeName(),
							cardList.stream().map(Card::getName).toList()
					);
				})
				.toList();
	}

	public ProductAndStoreAndCardAndUrl getProduct(Long productId) {
		ProductAndStore productAndStore = productRepository.findAllAndCardProductId(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		List<Card> cardList = storeCardMappingRepository.findByStoreId(productAndStore.storeId());

		return new ProductAndStoreAndCardAndUrl(
				productAndStore.productId(),
				productAndStore.storeProductId(),
				productAndStore.price(),
				productAndStore.pricePerMl(),
				productAndStore.discountRate(),
				productAndStore.size(),
				productAndStore.brand(),
				productAndStore.count(),
				productAndStore.taste(),
				productAndStore.storeName(),
				cardList.stream().map(Card::getName).toList(),
				switch (productAndStore.storeName()) {
					case "11번가" -> "https://www.11st.co.kr/products/" + productAndStore.storeProductId();
					default -> throw new IllegalArgumentException("Invalid store ID");
				}
		);
	}
}