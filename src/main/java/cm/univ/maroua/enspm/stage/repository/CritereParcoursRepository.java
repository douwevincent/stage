package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CritereParcoursRepository extends JpaRepository<CritereParcours, Long> {
	@Modifying
	@Query("delete from CritereParcours cp where cp.parcours.bareme is not null")
	void deleteForMigratedParcours();
}
