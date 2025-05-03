package hello.cokezet.temporary.domain.product.entity;

import hello.cokezet.temporary.domain.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "상품 ID", example = "1")
	private Long id = null;

	@Schema(description = "스토어 상품 ID", example = "1")
	@Column(nullable = false, unique = true)
	private Long storeProductId;

	@Schema(description = "스토어 ID", example = "1")
	@Column(nullable = false)
	private Long storeId;

	@Schema(description = "상품 가격", example = "1000")
	@Column(nullable = false)
	private int price;

	@Schema(description = "상품 가격(ml당)", example = "1000")
	@Column(nullable = false)
	private int pricePerMl;

	@Schema(description = "할인율", example = "10")
	@Column(nullable = false)
	int discountRate;

	@Schema(description = "상품 사이즈", example = "500ml")
	@Column(nullable = false)
	private String size;

	@Schema(description = "상품 브랜드", example = "코카콜라")
	@Column(nullable = false)
	private String brand;

	@Schema(description = "상품 재고", example = "100")
	@Column(nullable = false)
	private Integer count;

	@Schema(description = "상품 맛", example = "콜라")
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