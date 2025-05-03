package hello.cokezet.temporary.domain.store.entity;

import hello.cokezet.temporary.domain.store_card_mapping.entity.StoreCardMapping;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
