package hello.cokezet.temporary.domain.promotion.controller;

import hello.cokezet.temporary.domain.promotion.dto.response.StorePromotions;
import hello.cokezet.temporary.domain.promotion.service.PromotionService;
import hello.cokezet.temporary.global.common.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotions")
@Tag(name = "편의점 프로모션 API", description = "편의점 제로콜라 프로모션 정보 조회 API")
public class PromotionRestController {

    private final PromotionService promotionService;

    public PromotionRestController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @Operation(
            summary = "프로모션 정보 조회",
            description = "연도와 월을 기준으로 편의점별 제로콜라 프로모션 정보를 조회합니다. 파라미터가 없으면 현재 연월의 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResult<StorePromotions>> getPromotions(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {

        // 년도와 월이 지정되지 않았으면 현재 날짜 사용
        int[] currentYearMonth = promotionService.getCurrentYearAndMonth();
        int selectedYear = year != null ? year : currentYearMonth[0];
        int selectedMonth = month != null ? month : currentYearMonth[1];

        StorePromotions promotions = promotionService.getPromotionsForClient(selectedYear, selectedMonth);

        return ResponseEntity.ok(ApiResult.success(promotions));
    }
}
