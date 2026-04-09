package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodeStageRepository extends JpaRepository<PeriodeStage, Long> {

    Page<PeriodeStage> findByAnneeAcademiqueId(Long anneeAcademiqueId, Pageable pageable);

    /** Périodes dont la date de début correspond à la date de référence calculée (today - offsetDays). */
    List<PeriodeStage> findByTypeStageIdAndDateDebut(Long typeStageId, LocalDate dateDebut);

    /** Périodes dont la date de fin correspond à la date de référence calculée (today - offsetDays). */
    List<PeriodeStage> findByTypeStageIdAndDateFin(Long typeStageId, LocalDate dateFin);
    Optional<PeriodeStage> findByTypeStageIdAndAnneeAcademiqueId(Long typeStageId, Long anneeAcademiqueId);
}
