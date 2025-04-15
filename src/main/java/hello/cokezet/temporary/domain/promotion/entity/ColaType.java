package hello.cokezet.temporary.domain.promotion.entity;

import lombok.Getter;

@Getter
public enum ColaType {
    COCA("코카콜라"),
    PEPSI("펩시");

    private final String displayName;

    ColaType(String displayName) {
        this.displayName = displayName;
    }
}
