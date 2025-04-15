package hello.cokezet.temporary.domain.promotion.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionForm {
    private String store;
    private int year;
    private int month;
    private List<PromotionUpdate> promotions = new ArrayList<>();
}
