package hello.cokezet.temporary.domain.Product.entity;

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
	private String storeName;
	@Column(nullable = false)
	private int price;
	@Column(nullable = false)
	private String size;
	@Column(nullable = false)
	private String brand;

	public Product() { }

	public Product(Long storeProductId, String storeName, int price, String size, String brand) {
		this.storeProductId = storeProductId;
		this.storeName = storeName;
		this.price = price;
		this.size = size;
		this.brand = brand;
	}
}