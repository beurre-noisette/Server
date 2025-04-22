package hello.cokezet.temporary.domain.product.entity;

import hello.cokezet.temporary.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;
	@Column(nullable = false, unique = true)
	private Long storeProductId;
	@Column(nullable = false)
	private Long storeId;
	@Column(nullable = false)
	private int price;
	@Column(nullable = false)
	private int pricePerMl;
	@Column(nullable = false)
	int discountRate;
	@Column(nullable = false)
	private String size;
	@Column(nullable = false)
	private String brand;
	@Column(nullable = false)
	private Integer count;
	@Column(nullable = false)
	private String taste;

	public Product() { }

	public Product(
			Long storeProductId,
			Long storeId,
			int price,
			int pricePerMl,
			int discountRate,
			String size,
			String brand,
			Integer count,
			String taste

	) {
		this.storeProductId = storeProductId;
		this.storeId = storeId;
		this.price = price;
		this.pricePerMl = pricePerMl;
		this.discountRate = discountRate;
		this.size = size;
		this.brand = brand;
		this.count = count;
		this.taste = taste;
	}
}