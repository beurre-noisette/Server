package hello.cokezet.temporary.domain.benefit.repository;

import hello.cokezet.temporary.domain.benefit.model.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCompanyRepository extends JpaRepository<CardCompany, Long> {
}
