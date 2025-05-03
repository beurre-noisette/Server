package hello.cokezet.temporary.domain.store_card_mapping.entity;

import hello.cokezet.temporary.domain.card.entity.Card;
import hello.cokezet.temporary.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class StoreCardMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long storeId;
    private Long cardId;
}
