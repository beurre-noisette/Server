package hello.cokezet.temporary.domain.promotion.dto.response;

import hello.cokezet.temporary.domain.promotion.entity.PromotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로모션 정보")
public class PromotionInfo {

    @Schema(description = "제품 ID", example = "1")
    private Long productId;

    @Schema(description = "제품명", example = "코카콜라제로")
    private String productName;

    @Schema(description = "용량", example = "500ml")
    private String size;

    @Schema(description = "프로모션 타입", example = "ONE_PLUS_ONE")
    private PromotionType promotionType;

    @Schema(description = "가격", example = "1500")
    private Integer price;
}
