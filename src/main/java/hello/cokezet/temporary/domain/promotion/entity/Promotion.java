package hello.cokezet.temporary.domain.promotion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "promotions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"convenience_store_id", "zero_cola_id", "year", "month"}))
@Getter
@Setter
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "convenience_store_id", nullable = false)
    private ConvenienceStore convenienceStore;

    @ManyToOne
    @JoinColumn(name = "zero_cola_id", nullable = false)
    private ZeroCola zeroCola;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month; // 1-12

    @Column
    private Integer price; // null 가능 (가격 정보 없음)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType promotionType = PromotionType.NONE; // 기본값은 '없음'

    public Promotion() {}

    public Promotion(ConvenienceStore convenienceStore, ZeroCola zeroCola,
                     int year, int month, Integer price, PromotionType promotionType) {
        this.convenienceStore = convenienceStore;
        this.zeroCola = zeroCola;
        this.year = year;
        this.month = month;
        this.price = price;
        this.promotionType = promotionType != null ? promotionType : PromotionType.NONE;
    }
}
