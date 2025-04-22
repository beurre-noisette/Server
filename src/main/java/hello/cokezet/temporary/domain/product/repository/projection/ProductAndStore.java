package hello.cokezet.temporary.domain.product.repository.projection;

public record ProductAndStore(
		Long productId,
		Long storeProductId,
		int price,
		int pricePerMl,
		int discountRate,
		String size,
		String brand,
		Integer count,
		String taste,
		Long storeId,
		String storeName
) { }
