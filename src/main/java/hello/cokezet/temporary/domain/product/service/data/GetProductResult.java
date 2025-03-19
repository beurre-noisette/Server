package hello.cokezet.temporary.domain.product.service.data;

import hello.cokezet.temporary.domain.card.entity.Card;
import hello.cokezet.temporary.domain.product.entity.Product;

import java.util.List;

public record GetProductResult(
        Product product,
        List<Card> cardList
) { }
