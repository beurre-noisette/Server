package hello.cokezet.temporary.domain.promotion.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "zero_colas")
@Getter
public class ZeroCola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 제품명 (코카콜라 제로, 펩시 제로 등)

    @Column(nullable = false)
    private String size; // 용량 (250ml, 355ml, 500ml, 1.5L 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ColaType type; // 콜라 타입 (코카콜라, 펩시콜라)

    public ZeroCola() {}

    public ZeroCola(String name, String size, ColaType type) {
        this.name = name;
        this.size = size;
        this.type = type;
    }
}
