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
	@OneToOne
	private Store store;
	@Column(nullable = false)
	private int price;
	@Column(nullable = false)
	private String size;
	@Column(nullable = false)
	private String brand;
	@Column(nullable = false)
	private String count;
	@Column(nullable = false)
	private String taste;

	public Product() { }

	public Product(
			Long storeProductId,
			Store store, int price,
			String size,
			String brand,
			String count,
			String taste
	) {
		this.storeProductId = storeProductId;
		this.store = store;
		this.price = price;
		this.size = size;
		this.brand = brand;
		this.count = count;
		this.taste = taste;
	}
}