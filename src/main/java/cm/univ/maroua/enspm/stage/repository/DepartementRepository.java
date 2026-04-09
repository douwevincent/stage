package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repository JPA DepartementRepository.
 */
public interface DepartementRepository extends JpaRepository<Departement, Long> {
	Optional<Departement> findByCodeIgnoreCase(String code);
}
