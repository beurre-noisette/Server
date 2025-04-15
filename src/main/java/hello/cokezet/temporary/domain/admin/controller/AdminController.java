package hello.cokezet.temporary.domain.admin.controller;

import hello.cokezet.temporary.domain.promotion.dto.response.ProductView;
import hello.cokezet.temporary.domain.promotion.dto.request.PromotionForm;
import hello.cokezet.temporary.domain.promotion.dto.request.PromotionUpdate;
import hello.cokezet.temporary.domain.promotion.entity.ColaType;
import hello.cokezet.temporary.domain.promotion.entity.ConvenienceStore;
import hello.cokezet.temporary.domain.promotion.entity.Promotion;
import hello.cokezet.temporary.domain.promotion.entity.ZeroCola;
import hello.cokezet.temporary.domain.promotion.service.ConvenienceStoreService;
import hello.cokezet.temporary.domain.promotion.service.PromotionService;
import hello.cokezet.temporary.domain.promotion.service.ZeroColaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ConvenienceStoreService convenienceStoreService;
    private final ZeroColaService zeroColaService;
    private final PromotionService promotionService;

    public AdminController(ConvenienceStoreService convenienceStoreService,
                           ZeroColaService zeroColaService,
                           PromotionService promotionService) {
        this.convenienceStoreService = convenienceStoreService;
        this.zeroColaService = zeroColaService;
        this.promotionService = promotionService;
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // 프로모션 관리 페이지 (입력 페이지)
    @GetMapping("/promotions")
    public String promotionManagementPage(
            @RequestParam(defaultValue = "GS25") String store,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        // 년도와 월이 지정되지 않았으면 현재 날짜 사용
        int[] currentYearMonth = promotionService.getCurrentYearAndMonth();
        int selectedYear = year != null ? year : currentYearMonth[0];
        int selectedMonth = month != null ? month : currentYearMonth[1];

        // 편의점 목록
        List<ConvenienceStore> stores = convenienceStoreService.getAllStores();

        // 모든 제로콜라 제품 목록 가져오기
        List<ZeroCola> products = zeroColaService.getAllProducts();

        // 해당 편의점/년/월의 프로모션 정보
        Map<Long, Promotion> promotionMap = promotionService.getPromotionsByStoreAndDate(
                store, selectedYear, selectedMonth);

        // 화면에 표시할 데이터 준비
        List<ProductView> productViews = products.stream()
                .map(product -> {
                    ProductView viewDto = new ProductView(product);

                    // 프로모션 정보가 있으면 설정
                    Promotion promotion = promotionMap.get(product.getId());
                    if (promotion != null) {
                        viewDto.setPromotionType(promotion.getPromotionType());
                        viewDto.setPrice(promotion.getPrice());
                    }

                    return viewDto;
                })
                .toList();

        // 코카콜라와 펩시 제품 리스트 분리
        List<ProductView> cocaProducts = productViews.stream()
                .filter(p -> p.getColaType() == ColaType.COCA)
                .collect(Collectors.toList());

        List<ProductView> pepsiProducts = productViews.stream()
                .filter(p -> p.getColaType() == ColaType.PEPSI)
                .collect(Collectors.toList());

        // 연도 선택 옵션 (현재 연도 기준 +-2년)
        int currentYear = Year.now().getValue();
        List<Integer> years = IntStream.rangeClosed(currentYear - 2, currentYear + 2)
                .boxed().collect(Collectors.toList());

        // 월 선택 옵션 (1-12월)
        List<Integer> months = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("stores", stores);
        model.addAttribute("cocaProducts", cocaProducts);
        model.addAttribute("pepsiProducts", pepsiProducts);
        model.addAttribute("selectedStore", store);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("years", years);
        model.addAttribute("months", months);

        return "admin/promotions";
    }

    // 프로모션 조회 페이지
    @GetMapping("/promotions/view")
    public String viewPromotions(
            @RequestParam(defaultValue = "GS25") String store,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        // 년도와 월이 지정되지 않았으면 현재 날짜 사용
        int[] currentYearMonth = promotionService.getCurrentYearAndMonth();
        int selectedYear = year != null ? year : currentYearMonth[0];
        int selectedMonth = month != null ? month : currentYearMonth[1];

        // 편의점 목록
        List<ConvenienceStore> stores = convenienceStoreService.getAllStores();

        // 해당 편의점/년/월의 프로모션 정보
        Map<Long, Promotion> promotionMap = promotionService.getPromotionsByStoreAndDate(
                store, selectedYear, selectedMonth);

        boolean hasPromotionData = !promotionMap.isEmpty();

        // 프로모션이 설정된 제품만 조회
        List<ProductView> promotionProducts = new ArrayList<>();
        if (hasPromotionData) {
            for (Map.Entry<Long, Promotion> entry : promotionMap.entrySet()) {
                Long productId = entry.getKey();
                Promotion promotion = entry.getValue();

                if (promotion.getPromotionType() != null &&
                        !promotion.getPromotionType().name().equals("NONE")) {

                    ZeroCola product = zeroColaService.getProductById(productId);
                    ProductView viewDto = new ProductView(product);
                    viewDto.setPromotionType(promotion.getPromotionType());
                    viewDto.setPrice(promotion.getPrice());
                    promotionProducts.add(viewDto);
                }
            }
        }

        // 코카콜라와 펩시 제품 리스트 분리
        List<ProductView> cocaPromotions = promotionProducts.stream()
                .filter(p -> p.getColaType() == ColaType.COCA)
                .collect(Collectors.toList());

        List<ProductView> pepsiPromotions = promotionProducts.stream()
                .filter(p -> p.getColaType() == ColaType.PEPSI)
                .collect(Collectors.toList());

        // 연도 선택 옵션 (현재 연도 기준 +-2년)
        int currentYear = Year.now().getValue();
        List<Integer> years = IntStream.rangeClosed(currentYear - 2, currentYear + 2)
                .boxed().collect(Collectors.toList());

        // 월 선택 옵션 (1-12월)
        List<Integer> months = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("stores", stores);
        model.addAttribute("hasPromotionData", hasPromotionData);
        model.addAttribute("cocaPromotions", cocaPromotions);
        model.addAttribute("pepsiPromotions", pepsiPromotions);
        model.addAttribute("selectedStore", store);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("years", years);
        model.addAttribute("months", months);

        return "admin/promotion-view";
    }

    // 프로모션 저장 처리
    @PostMapping("/promotions")
    public String savePromotions(PromotionForm form) {
        List<PromotionUpdate> validPromotions = new ArrayList<>();

        // NULL 체크 및 유효한 데이터만 필터링
        for (PromotionUpdate dto : form.getPromotions()) {
            if (dto.getProductId() != null) {
                validPromotions.add(dto);
            }
        }

        // 프로모션 정보 저장
        promotionService.savePromotions(
                form.getStore(), form.getYear(), form.getMonth(), validPromotions);

        return "redirect:/admin/promotions/view?store=" + form.getStore()
                + "&year=" + form.getYear()
                + "&month=" + form.getMonth();
    }

    /**
     * 프로모션 삭제 처리
     * 특정 편의점, 연/월, 제품에 해당하는 프로모션을 삭제하고 조회 페이지로 리다이렉트합니다.
     *
     * @param store     편의점 이름
     * @param year      연도
     * @param month     월
     * @param productId 제품 ID
     * @return 프로모션 조회 페이지로 리다이렉트
     */
    @GetMapping("/promotions/delete")
    public String deletePromotion(
            @RequestParam String store,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long productId) {

        promotionService.deletePromotion(store, year, month, productId);

        return "redirect:/admin/promotions/view?store=" + store
                + "&year=" + year
                + "&month=" + month;
    }
}