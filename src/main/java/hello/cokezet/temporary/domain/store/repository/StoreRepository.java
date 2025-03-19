package hello.cokezet.temporary.domain.store.repository;

import hello.cokezet.temporary.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findByName(String s);
}

