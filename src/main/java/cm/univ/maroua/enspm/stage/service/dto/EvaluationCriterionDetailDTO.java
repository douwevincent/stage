package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * Detail d'une note de critere dans une fiche d'evaluation.
 */
public record EvaluationCriterionDetailDTO(
        Long critereId,
        String critere,
        Float coefficient,
        Integer note,
        String commentaire) implements Serializable {
}
