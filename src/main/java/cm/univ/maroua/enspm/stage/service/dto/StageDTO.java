package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record StageDTO(
                Long id,
                Long etudiantId,
                Long entrepriseId,
                String ville,
                String adresse,
                Long encadreurId,
                Long typeStageId,
                LocalDate dateDebut,
                LocalDate dateFin,
                Long anneeAcademiqueId,
                Long sessionEvaluationId) implements Serializable {
}
