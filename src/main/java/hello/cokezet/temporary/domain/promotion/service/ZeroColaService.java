package hello.cokezet.temporary.domain.promotion.service;

import hello.cokezet.temporary.domain.promotion.entity.ZeroCola;
import hello.cokezet.temporary.domain.promotion.repository.ZeroColaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ZeroColaService {

    private final ZeroColaRepository zeroColaRepository;

    public ZeroColaService(ZeroColaRepository zeroColaRepository) {
        this.zeroColaRepository = zeroColaRepository;
    }

    // 모든 제로콜라 제품 조회
    public List<ZeroCola> getAllProducts() {
        return zeroColaRepository.findAll();
    }

    // ID로 제품 조회
    public ZeroCola getProductById(Long id) {
        return zeroColaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제품입니다: " + id));
    }
}
