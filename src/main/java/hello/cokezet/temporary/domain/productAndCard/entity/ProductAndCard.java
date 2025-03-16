package hello.cokezet.temporary.domain.productAndCard.entity;

import hello.cokezet.temporary.domain.Product.entity.Product;
import hello.cokezet.temporary.domain.card.entity.Card;
import jakarta.persistence.*;

@Entity
public class ProductAndCard {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Card card;

	@ManyToOne
	private Product product;
}
