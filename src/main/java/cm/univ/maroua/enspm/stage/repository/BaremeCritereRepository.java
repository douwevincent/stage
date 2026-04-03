package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.BaremeCritere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaremeCritereRepository extends JpaRepository<BaremeCritere, Long> {
    Page<BaremeCritere> findAllByBaremeId(Long baremeId, Pageable pageable);

    List<BaremeCritere> findByBaremeId(Long baremeId);

    Optional<BaremeCritere> findByBaremeIdAndCritereId(Long baremeId, Long critereId);

    /**
     * Verifie si un critere est utilise dans au moins un bareme.
     */
    boolean existsByCritereId(Long critereId);
}