package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repository JPA InscriptionRepository.
 */
public interface InscriptionRepository extends JpaRepository<Inscription, Long>, JpaSpecificationExecutor<Inscription> {
    boolean existsByAnneeAcademiqueIdAndEtudiantIdAndParcoursId(Long anneeAcademiqueId, Long etudiantId, Long parcoursId);

    long countByAnneeAcademiqueId(Long anneeAcademiqueId);

    Optional<Inscription> findFirstByEtudiantIdAndAnneeAcademiqueIdOrderByIdDesc(Long etudiantId, Long anneeAcademiqueId);
}
