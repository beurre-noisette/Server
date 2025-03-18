package hello.cokezet.temporary.domain.productAndCard.controller;

import hello.cokezet.temporary.domain.productAndCard.entity.ProductAndCard;
import hello.cokezet.temporary.domain.productAndCard.service.ProductAndCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productAndCard")
@Tag(name = "온라인 스토어 상품 API", description = "온라인 스토어 API를 제공합니다.")
public class ProductAndCardRestController {

	private final Logger log = LoggerFactory.getLogger(ProductAndCardRestController.class);
	private final ProductAndCardService productAndCardService;

	public ProductAndCardRestController(ProductAndCardService productAndCardService) {
		this.productAndCardService = productAndCardService;
	}

	@Operation(
			summary = "온라인 스토어 상품 조회 API",
			description = """
            11번가 상품을 조회합니다.
            price는 ml 당 가격입니다.
    """)
	@GetMapping
	public ResponseEntity<List<GetProductAndCard>> getProductAndCardList() {
		List<ProductAndCard> productAndCardList = productAndCardService.getProductAndCardList();
		log.info("productAndCardList: {}", productAndCardList);

		return ResponseEntity.ok(
				List.of(
						new GetProductAndCard(
								"코카콜라",
								"11번가",
								1000,
								"500ml",
								"코카콜라",
								10,
								List.of("신한카드", "삼성카드")
						)
				)
		);
	}

	public record GetProductAndCard(
		@Schema(defaultValue = "코카콜라", description = "상품명")
		String productName,
		@Schema(defaultValue = "11번가", description = "온라인 스토어")
		String storeName,
		@Schema(defaultValue = "1000", description = "ml 당 가격")
		int price,
		@Schema(defaultValue = "500ml", description = "용량")
		String size,
		@Schema(defaultValue = "코카콜라", description = "브랜드")
		String brand,
		@Schema(defaultValue = "10", description = "할인율")
		int discountRate,
		@Schema(description = "카드 할인 목록")
		List<String> cards
	) { }
}
