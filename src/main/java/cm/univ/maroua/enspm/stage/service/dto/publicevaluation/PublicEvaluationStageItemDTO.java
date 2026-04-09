package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO d'evaluation publique PublicEvaluationStageItemDTO.
 */
public record PublicEvaluationStageItemDTO(
        Long stageId,
        Long sessionId,
        String etudiantNom,
        String matricule,
        String entrepriseNom,
        LocalDate dateDebut,
        LocalDate dateFin,
        LocalDate dateLimite) implements Serializable {
}
