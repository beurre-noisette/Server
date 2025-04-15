package hello.cokezet.temporary.domain.promotion.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "convenience_stores")
@Getter
public class ConvenienceStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // GS25, CU, 세븐일레븐

    public ConvenienceStore() {

    }

    public ConvenienceStore(String name) {
        this.name = name;
    }
}
