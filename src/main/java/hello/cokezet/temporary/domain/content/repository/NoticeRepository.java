package hello.cokezet.temporary.domain.content.repository;

import hello.cokezet.temporary.domain.content.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByImportantDescCreatedAtDesc();
}
