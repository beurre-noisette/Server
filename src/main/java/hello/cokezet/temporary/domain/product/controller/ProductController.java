package hello.cokezet.temporary.domain.product.controller;

import hello.cokezet.temporary.domain.product.controller.dto.request.GetProductListRequest;
import hello.cokezet.temporary.domain.product.service.ProductService;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "온라인 스토어 상품 API", description = "온라인 스토어 API를 제공합니다.")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
        summary = "온라인 스토어 상품 조회 API",
        description = """
            11번가 상품을 조회합니다.
        """)
    @GetMapping
    public ResponseEntity<List<GetProductResponse>> getProductList(
            @Valid @ParameterObject GetProductListRequest request
    ) {
        List<GetProductResult> resultList = productService.getProductList();

        return ResponseEntity.ok(
                        resultList.stream()
                        .map(result -> new GetProductResponse(
                                result.id(),
                                result.storeProductId(),
                                result.price(),
                                result.pricePerMl(),
                                result.discountRate(),
                                result.size(),
                                result.brand(),
                                result.count(),
                                result.taste(),
                                result.storeName(),
                                result.cardNameList()
                        ))
                        .toList()
        );
    }

    public record GetProductResponse(
            @Schema(description = "zet 상품 ID", example = "1")
            Long productId,
            @Schema(description = "온라인 스토어 상품 ID", example = "1")
            Long storeProductId,
            @Schema(defaultValue = "1000", description = "가격")
            int price,
            @Schema(defaultValue = "154", description = "미리당 가격")
            int pricePerMl,
            @Schema(defaultValue = "10", description = "할인율")
            int discountRate,
            @Schema(defaultValue = "500ml", description = "용량")
            String size,
            @Schema(defaultValue = "코카콜라", description = "브랜드")
            String brand,
            @Schema(defaultValue = "24", description = "묶음 갯수")
            int count,
            @Schema(defaultValue = "라임", description = "맛")
            String taste,
            @Schema(description = "온라인 스토어", example = "11번가")
            String storeName,
            @Schema(description = "카드사 목록", example = "신한카드, 삼성카드")
            List<String> cardNameList
    ) { }
}
