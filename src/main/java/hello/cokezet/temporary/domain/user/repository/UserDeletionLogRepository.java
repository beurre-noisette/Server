package hello.cokezet.temporary.domain.user.repository;

import hello.cokezet.temporary.domain.user.model.UserDeletionLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 회원탈퇴 로그 레포지토리
 */
public interface UserDeletionLogRepository extends JpaRepository<UserDeletionLog, Long> {
}
