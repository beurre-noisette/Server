package hello.cokezet.temporary.domain.card.repository;

import hello.cokezet.temporary.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> { }
