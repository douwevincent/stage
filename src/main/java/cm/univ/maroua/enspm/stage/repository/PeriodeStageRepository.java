package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodeStageRepository extends JpaRepository<PeriodeStage, Long> {
}
