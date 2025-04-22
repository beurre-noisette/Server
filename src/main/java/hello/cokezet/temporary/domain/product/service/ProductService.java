package hello.cokezet.temporary.domain.product.service;

import hello.cokezet.temporary.domain.card.entity.Card;
import hello.cokezet.temporary.domain.card.repository.CardRepository;
import hello.cokezet.temporary.domain.product.entity.Product;
import hello.cokezet.temporary.domain.product.repository.ProductRepository;
import hello.cokezet.temporary.domain.product.repository.projection.ProductAndStore;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import hello.cokezet.temporary.domain.store_card_mapping.entity.StoreCardMapping;
import hello.cokezet.temporary.domain.store_card_mapping.repository.StoreCardMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final StoreCardMappingRepository storeCardMappingRepository;

	public ProductService(
			ProductRepository productRepository, CardRepository cardRepository,
			StoreCardMappingRepository storeCardMappingRepository
	) {
		this.productRepository = productRepository;
		this.storeCardMappingRepository = storeCardMappingRepository;
	}

	public List<GetProductResult> getProductList() {
		List<ProductAndStore> productAndStoreList = productRepository.findAllAndCard();

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
}