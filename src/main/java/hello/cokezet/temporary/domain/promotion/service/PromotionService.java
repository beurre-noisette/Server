package hello.cokezet.temporary.domain.promotion.service;

import hello.cokezet.temporary.domain.promotion.dto.request.PromotionUpdate;
import hello.cokezet.temporary.domain.promotion.dto.response.PromotionInfo;
import hello.cokezet.temporary.domain.promotion.dto.response.StorePromotions;
import hello.cokezet.temporary.domain.promotion.entity.ConvenienceStore;
import hello.cokezet.temporary.domain.promotion.entity.Promotion;
import hello.cokezet.temporary.domain.promotion.entity.PromotionType;
import hello.cokezet.temporary.domain.promotion.entity.ZeroCola;
import hello.cokezet.temporary.domain.promotion.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ConvenienceStoreService convenienceStoreService;
    private final ZeroColaService zeroColaService;

    public PromotionService(PromotionRepository promotionRepository,
                            ConvenienceStoreService convenienceStoreService,
                            ZeroColaService zeroColaService) {
        this.promotionRepository = promotionRepository;
        this.convenienceStoreService = convenienceStoreService;
        this.zeroColaService = zeroColaService;
    }

    // 현재 연도와 월을 가져오는 메서드
    public int[] getCurrentYearAndMonth() {
        LocalDate now = LocalDate.now();
        return new int[]{now.getYear(), now.getMonthValue()};
    }

    // 특정 편의점, 년, 월의 프로모션 정보 조회
    @Transactional(readOnly = true)
    public Map<Long, Promotion> getPromotionsByStoreAndDate(String storeName, int year, int month) {
        ConvenienceStore store = convenienceStoreService.getStoreByName(storeName);
        List<Promotion> promotions = promotionRepository.findByConvenienceStoreAndYearAndMonth(
                store, year, month);

        // 제품ID를 키로 하는 맵으로 변환
        return promotions.stream()
                .collect(Collectors.toMap(
                        p -> p.getZeroCola().getId(),
                        p -> p
                ));
    }

    // 프로모션 정보 저장 (생성 또는 업데이트)
    @Transactional
    public void savePromotion(String storeName, int year, int month, PromotionUpdate dto) {
        // 프로모션 타입이 NONE이면 처리하지 않음
        if (dto.getPromotionType() == PromotionType.NONE) {
            return;
        }

        ConvenienceStore store = convenienceStoreService.getStoreByName(storeName);
        ZeroCola zeroCola = zeroColaService.getProductById(dto.getProductId());

        // 기존 프로모션이 있는지 확인
        Optional<Promotion> existingPromotion = promotionRepository
                .findByConvenienceStoreAndZeroColaAndYearAndMonth(store, zeroCola, year, month);

        if (existingPromotion.isPresent()) {
            // 기존 프로모션 업데이트
            Promotion promotion = existingPromotion.get();
            promotion.setPromotionType(dto.getPromotionType());
            promotion.setPrice(dto.getPrice());
            promotionRepository.save(promotion);
        } else {
            // 새 프로모션 생성
            Promotion newPromotion = new Promotion(store, zeroCola, year, month,
                    dto.getPrice(), dto.getPromotionType());
            promotionRepository.save(newPromotion);
        }
    }

    // 여러 프로모션 정보 일괄 저장
    @Transactional
    public void savePromotions(String storeName, int year, int month, List<PromotionUpdate> dtos) {
        // 프로모션 타입이 NONE이 아닌 항목만 필터링
        dtos.stream()
                .filter(dto -> dto.getProductId() != null && dto.getPromotionType() != PromotionType.NONE)
                .forEach(dto -> savePromotion(storeName, year, month, dto));
    }

    /**
     * 클라이언트용 프로모션 정보 조회 메서드
     * 특정 연/월에 해당하는 모든 편의점의 프로모션 정보를 조회합니다.
     * 프로모션 타입이 'NONE'이 아닌 항목만 포함됩니다.
     *
     * @param year  조회할 연도
     * @param month 조회할 월 (1-12)
     * @return 편의점별 프로모션 정보가 담긴 객체
     */
    @Transactional(readOnly = true)
    public StorePromotions getPromotionsForClient(int year, int month) {
        Map<String, List<PromotionInfo>> storePromotionMap = new HashMap<>();

        // 모든 편의점 목록 조회
        List<ConvenienceStore> stores = convenienceStoreService.getAllStores();

        for (ConvenienceStore store : stores) {
            // 각 편의점의 프로모션 정보 조회
            Map<Long, Promotion> promotionMap = getPromotionsByStoreAndDate(store.getName(), year, month);

            // 프로모션 정보가 있는 제품만 필터링
            List<PromotionInfo> promotions = new ArrayList<>();

            for (Map.Entry<Long, Promotion> entry : promotionMap.entrySet()) {
                Promotion promotion = entry.getValue();

                // NONE이 아닌 프로모션만 포함
                if (promotion.getPromotionType() != PromotionType.NONE) {
                    ZeroCola product = promotion.getZeroCola();

                    PromotionInfo info = new PromotionInfo(
                            product.getId(),
                            product.getName(),
                            product.getSize(),
                            product.getType().getDisplayName(),
                            promotion.getPromotionType(),
                            promotion.getPrice()
                    );

                    promotions.add(info);
                }
            }

            // 프로모션이 있는 경우만 맵에 추가
            if (!promotions.isEmpty()) {
                storePromotionMap.put(store.getName(), promotions);
            }
        }

        return new StorePromotions(year, month, storePromotionMap);
    }

    /**
     * 프로모션 삭제 메서드
     * 특정 편의점, 연/월, 제품에 해당하는 프로모션 정보를 삭제합니다.
     *
     * @param storeName 편의점 이름
     * @param year      연도
     * @param month     월 (1-12)
     * @param productId 제품 ID
     */
    @Transactional
    public void deletePromotion(String storeName, int year, int month, Long productId) {
        ConvenienceStore store = convenienceStoreService.getStoreByName(storeName);
        ZeroCola zeroCola = zeroColaService.getProductById(productId);

        Optional<Promotion> existingPromotion = promotionRepository
                .findByConvenienceStoreAndZeroColaAndYearAndMonth(store, zeroCola, year, month);

        existingPromotion.ifPresent(promotionRepository::delete);
    }
}
