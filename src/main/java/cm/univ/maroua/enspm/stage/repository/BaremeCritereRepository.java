package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.BaremeCritere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaremeCritereRepository extends JpaRepository<BaremeCritere, Long> {
    Page<BaremeCritere> findAllByBaremeId(Long baremeId, Pageable pageable);
}