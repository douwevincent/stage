package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Repository JPA NoteRepository.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {
    void deleteBySessionId(Long sessionId);

    List<Note> findBySessionId(Long sessionId);
}
