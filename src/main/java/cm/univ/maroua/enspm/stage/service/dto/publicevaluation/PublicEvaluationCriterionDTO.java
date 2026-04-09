package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import java.io.Serializable;

/**
 * DTO d'evaluation publique PublicEvaluationCriterionDTO.
 */
public record PublicEvaluationCriterionDTO(
        Long critereId,
        String libelle,
        String categorie,
        Float coefficient) implements Serializable {
}
