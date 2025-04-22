package hello.cokezet.temporary.domain.product.controller;

import hello.cokezet.temporary.domain.product.controller.dto.request.GetProductListRequest;
import hello.cokezet.temporary.domain.product.controller.dto.response.GetProductResponse;
import hello.cokezet.temporary.domain.product.service.ProductService;
import hello.cokezet.temporary.domain.product.service.data.GetProductResult;
import hello.cokezet.temporary.domain.product.service.data.ProductAndStoreAndCard;
import hello.cokezet.temporary.domain.product.service.data.ProductAndStoreAndCardAndUrl;
import hello.cokezet.temporary.global.common.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<ApiResult<List<GetProductResponse>>> getProductList(
            @Valid @ParameterObject GetProductListRequest request
    ) {
        List<GetProductResult> resultList = productService.getProductList(request);

        return ResponseEntity.ok(ApiResult.success(
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
                )

        );
    }

    @GetMapping("/{productId}")
    @Operation(
        summary = "온라인 스토어 상품 상세 조회 API",
        description = """
            11번가 상품을 상세 조회합니다.
        """)
    public ResponseEntity<ApiResult<ProductAndStoreAndCardAndUrl>> getProduct(
            @PathVariable("productId") Long productId
    ) {
        ProductAndStoreAndCardAndUrl result = productService.getProduct(productId);

        return ResponseEntity.ok(
                ApiResult.success(
                    result
                )
        );
    }

}
