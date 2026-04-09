package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repository JPA EntrepriseRepository.
 */
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    List<Entreprise> findByNomContainingIgnoreCase(String nom);
    Optional<Entreprise> findByNomIgnoreCase(String nom);
}
