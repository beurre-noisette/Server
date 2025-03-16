package hello.cokezet.temporary.domain.productAndCard.service;

import hello.cokezet.temporary.domain.productAndCard.entity.ProductAndCard;
import hello.cokezet.temporary.domain.productAndCard.repository.ProductAndCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAndCardService {

	private final ProductAndCardRepository productAndCardRepository;

	public ProductAndCardService(ProductAndCardRepository productAndCardRepository) {
		this.productAndCardRepository = productAndCardRepository;
	}

	public List<ProductAndCard> getProductAndCardList() {
		return productAndCardRepository.findAll();
	}
}
