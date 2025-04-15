package hello.cokezet.temporary.domain.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "편의점별 프로모션 정보")
public class StorePromotions {

    @Schema(description = "조회 연도", example = "2023")
    private int year;

    @Schema(description = "조회 월", example = "4")
    private int month;

    /**
     * key: 편의점 이름
     * value: 프로모션 목록
     */
    @Schema(description = "편의점별 프로모션 목록",
            example = "{'GS25': [{...}], 'CU': [{...}], '세븐일레븐': [{...}]}")
    private Map<String, List<PromotionInfo>> storePromotions;
}
