package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record PeriodeStageDTO(Long id, TypeStageDTO typeStage, AnneeAcademiqueDTO anneeAcademique, LocalDate dateDebut,
        LocalDate dateFin) implements Serializable {
}
