package hello.cokezet.temporary.domain.product.controller;

import hello.cokezet.temporary.domain.product.service.ProductService;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import hello.cokezet.temporary.domain.store.entity.Store;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            price는 ml 당 가격입니다.
        """)
    @GetMapping
    public ResponseEntity<List<GetProductResponse>> getProductList() {
        List<GetProductResult> resultList = productService.getProductList();

        return ResponseEntity.ok(
                        resultList.stream()
                        .map(result -> new GetProductResponse(
                                result.product().getStore(),
                                result.product().getPrice(),
                                result.product().getSize(),
                                result.product().getBrand(),
                                result.product().getCount(),
                                result.product().getTaste()
                        ))
                        .toList()
        );
    }

    public record GetProductResponse(
            @Schema(description = "온라인 스토어")
            Store store,
            @Schema(defaultValue = "1000", description = "ml 당 가격")
            int price,
            @Schema(defaultValue = "500ml", description = "용량")
            String size,
            @Schema(defaultValue = "코카콜라", description = "브랜드")
            String brand,
            @Schema(defaultValue = "24", description = "묶음 갯수")
            String count,
            @Schema(defaultValue = "라임", description = "맛")
            String taste
    ) { }
}
