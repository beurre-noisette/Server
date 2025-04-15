package hello.cokezet.temporary.global.config;

import hello.cokezet.temporary.domain.promotion.entity.ColaType;
import hello.cokezet.temporary.domain.promotion.entity.ConvenienceStore;
import hello.cokezet.temporary.domain.promotion.entity.ZeroCola;
import hello.cokezet.temporary.domain.promotion.repository.ConvenienceStoreRepository;
import hello.cokezet.temporary.domain.promotion.repository.ZeroColaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ConveniencePromotionDataInitializer {

    @Bean
    public CommandLineRunner initData(ConvenienceStoreRepository convenienceStoreRepository,
                                      ZeroColaRepository zeroColaRepository) {
        return args -> {
            // 편의점 데이터 초기화
            if (convenienceStoreRepository.count() == 0) {
                convenienceStoreRepository.saveAll(Arrays.asList(
                        new ConvenienceStore("GS25"),
                        new ConvenienceStore("CU"),
                        new ConvenienceStore("세븐일레븐")
                ));

                System.out.println("편의점 데이터 초기화 완료");
            }

            // 제로콜라 제품 데이터 초기화
            if (zeroColaRepository.count() == 0) {
                // 코카콜라 제품
                zeroColaRepository.save(new ZeroCola("코카콜라제로제로레몬캔", "350ml", ColaType.COCA));
                zeroColaRepository.save(new ZeroCola("코카콜라제로제로캔", "350ml", ColaType.COCA));
                zeroColaRepository.save(new ZeroCola("코카콜라제로캔", "350ml", ColaType.COCA));
                zeroColaRepository.save(new ZeroCola("코카콜라제로", "1.5L", ColaType.COCA));
                zeroColaRepository.save(new ZeroCola("코카콜라제로", "500ml", ColaType.COCA));
                zeroColaRepository.save(new ZeroCola("코카콜라제로레몬", "500ml", ColaType.COCA));
                zeroColaRepository.save(new ZeroCola("코카콜라제로체리", "500ml", ColaType.COCA));

                // 펩시콜라 제품
                zeroColaRepository.save(new ZeroCola("펩시제로모히토", "500ml", ColaType.PEPSI));
                zeroColaRepository.save(new ZeroCola("펩시제로라임", "500ml", ColaType.PEPSI));
                zeroColaRepository.save(new ZeroCola("펩시제로제로", "500ml", ColaType.PEPSI));
                zeroColaRepository.save(new ZeroCola("펩시제로라임", "355ml", ColaType.PEPSI));

                System.out.println("제로콜라 제품 데이터 초기화 완료");
            }
        };
    }
}