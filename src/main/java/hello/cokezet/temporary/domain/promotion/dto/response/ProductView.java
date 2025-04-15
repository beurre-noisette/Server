package hello.cokezet.temporary.domain.promotion.dto.response;

import hello.cokezet.temporary.domain.promotion.entity.ColaType;
import hello.cokezet.temporary.domain.promotion.entity.PromotionType;
import hello.cokezet.temporary.domain.promotion.entity.ZeroCola;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductView {

    private Long id;
    private String name;
    private String size;
    private ColaType colaType;
    private PromotionType promotionType = PromotionType.NONE;
    private Integer price;

    public ProductView(ZeroCola zeroCola) {
        this.id = zeroCola.getId();
        this.name = zeroCola.getName();
        this.size = zeroCola.getSize();
        this.colaType = zeroCola.getType();
    }
}
