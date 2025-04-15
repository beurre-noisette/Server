package hello.cokezet.temporary.domain.promotion.entity;

import lombok.Getter;

@Getter
public enum PromotionType {
    NONE("없음"),
    ONE_PLUS_ONE("1+1"),
    TWO_PLUS_ONE("2+1");

    private final String displayName;

    PromotionType(String displayName) {
        this.displayName = displayName;
    }
}
