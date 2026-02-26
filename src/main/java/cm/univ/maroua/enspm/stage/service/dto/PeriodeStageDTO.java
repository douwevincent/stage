package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record PeriodeStageDTO(Long id, Long typeStageId, Long anneeAcademiqueId, LocalDate dateDebut,
                LocalDate dateFin) implements Serializable {
}
