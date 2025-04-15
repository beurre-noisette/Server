package hello.cokezet.temporary.domain.promotion.service;

import hello.cokezet.temporary.domain.promotion.entity.ConvenienceStore;
import hello.cokezet.temporary.domain.promotion.repository.ConvenienceStoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConvenienceStoreService {

    private final ConvenienceStoreRepository convenienceStoreRepository;

    public ConvenienceStoreService(ConvenienceStoreRepository convenienceStoreRepository) {
        this.convenienceStoreRepository = convenienceStoreRepository;
    }

    // 모든 편의점 조회
    public List<ConvenienceStore> getAllStores() {
        return convenienceStoreRepository.findAll();
    }

    // 편의점 이름으로 조회
    public ConvenienceStore getStoreByName(String name) {
        return convenienceStoreRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 편의점입니다: " + name));
    }
}
