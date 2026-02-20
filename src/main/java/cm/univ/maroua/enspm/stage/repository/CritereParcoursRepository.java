package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CritereParcoursRepository extends JpaRepository<CritereParcours, CritereParcours.CritereParcoursId> {
}
