package hello.cokezet.temporary.domain.content.repository;

import hello.cokezet.temporary.domain.content.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n ORDER BY n.important DESC, n.createdAt DESC")
    List<Notice> findAllByOrderByImportantDescCreatedAtDesc();
}
