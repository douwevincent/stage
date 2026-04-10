package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Ensemble de criteres regroupes par categorie.
 */
public record EvaluationCategoryDetailDTO(
        String categorie,
        List<EvaluationCriterionDetailDTO> criteres) implements Serializable {
}
