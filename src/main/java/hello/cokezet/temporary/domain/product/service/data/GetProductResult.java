package hello.cokezet.temporary.domain.product.service.data;

import hello.cokezet.temporary.domain.card.entity.Card;
import hello.cokezet.temporary.domain.product.entity.Product;

import java.util.List;

public record GetProductResult(
        Long id,
		Long storeProductId,
		int price,
		int pricePerMl,
		int discountRate,
		String size,
		String brand,
		Integer count,
		String taste,
		String storeName,
		List<String> cardNameList
) { }
