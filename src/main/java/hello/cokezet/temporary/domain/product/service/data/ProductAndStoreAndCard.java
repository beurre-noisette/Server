package hello.cokezet.temporary.domain.product.service.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProductAndStoreAndCard(
		@Schema(description = "zet 상품 ID", example = "1")
		Long id,
		@Schema(description = "온라인 스토어 상품 ID", example = "1")
		Long storeProductId,
		@Schema(defaultValue = "1000", description = "가격")
		int price,
		@Schema(defaultValue = "154", description = "미리당 가격")
		int pricePerMl,
		@Schema(defaultValue = "10", description = "할인율")
		int discountRate,
		@Schema(defaultValue = "500", description = "용량")
		String size,
		@Schema(defaultValue = "코카콜라", description = "브랜드")
		String brand,
		@Schema(defaultValue = "24", description = "묶음 갯수")
		Integer count,
		@Schema(defaultValue = "라임", description = "맛")
		String taste,
		@Schema(description = "온라인 스토어", example = "11번가")
		String storeName,
		@Schema(description = "카드사 목록", example = "신한, 삼성")
		List<String> cardNameList
) {

}
