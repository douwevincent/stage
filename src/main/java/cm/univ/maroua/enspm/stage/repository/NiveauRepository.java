package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repository JPA NiveauRepository.
 */
public interface NiveauRepository extends JpaRepository<Niveau, Long> {
	Optional<Niveau> findByLibelleIgnoreCase(String libelle);
}
