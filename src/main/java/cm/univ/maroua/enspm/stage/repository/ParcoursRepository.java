package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParcoursRepository extends JpaRepository<Parcours, Long>, JpaSpecificationExecutor<Parcours> {
	Optional<Parcours> findBySpecialiteIdAndNiveauId(Long specialiteId, Long niveauId);
}
