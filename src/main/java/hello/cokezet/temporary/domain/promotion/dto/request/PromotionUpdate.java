package hello.cokezet.temporary.domain.promotion.dto.request;

import hello.cokezet.temporary.domain.promotion.entity.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionUpdate {
    private Long productId;
    private PromotionType promotionType = PromotionType.NONE; // 기본값은 '없음'
    private Integer price; // null 가능 (가격 정보 없음)
}
