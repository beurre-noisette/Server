package hello.cokezet.temporary.domain.user.repository;

import hello.cokezet.temporary.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 삭제 되지 않은 사용자 이메일로 조회
     */
    Optional<User> findByEmail(String email);
}
