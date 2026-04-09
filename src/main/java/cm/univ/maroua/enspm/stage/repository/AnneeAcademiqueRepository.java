package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repository JPA AnneeAcademiqueRepository.
 */
public interface AnneeAcademiqueRepository extends JpaRepository<AnneeAcademique, Long> {

    Optional<AnneeAcademique> findByActifTrue();

    @Modifying
    @Query("UPDATE AnneeAcademique a SET a.actif = false WHERE a.actif = true")
    void deactivateAll();
}
