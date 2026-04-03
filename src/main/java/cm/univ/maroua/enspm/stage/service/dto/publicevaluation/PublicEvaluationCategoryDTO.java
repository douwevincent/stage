package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import java.io.Serializable;
import java.util.List;

public record PublicEvaluationCategoryDTO(
        String categorie,
        List<PublicEvaluationCriterionDTO> criteres) implements Serializable {
}
