package hello.cokezet.temporary.domain.product.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetProductListRequest(
		@Schema(description = "브랜드", example = "코카콜라")
		String brand,
		@Schema(description = "용량", example = "190")
		int productSize,
		@Schema(description = "온라인 쇼핑몰 이름", example = "11번가")
		String storeName,
		@Schema(description = "할인 타입", example = "제트픽")
		String discountType,
		@Schema(description = "카트사 이름", example = "신한")
		String cardName,
		@Schema(description = "페이지 0부터 시작", example = "0")
		int page,
		@Schema(description = "페이지당 상품 개수", example = "10")
		int size
) {

}
