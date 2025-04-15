package hello.cokezet.temporary.domain.promotion.repository;

import hello.cokezet.temporary.domain.promotion.entity.ConvenienceStore;
import hello.cokezet.temporary.domain.promotion.entity.Promotion;
import hello.cokezet.temporary.domain.promotion.entity.ZeroCola;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    // 특정 편의점, 년, 월의 모든 프로모션 조회
    List<Promotion> findByConvenienceStoreAndYearAndMonth(
            ConvenienceStore convenienceStore, int year, int month);

    // 특정 편의점, 제품, 년, 월의 프로모션 조회
    Optional<Promotion> findByConvenienceStoreAndZeroColaAndYearAndMonth(
            ConvenienceStore convenienceStore, ZeroCola zeroCola, int year, int month);
}
