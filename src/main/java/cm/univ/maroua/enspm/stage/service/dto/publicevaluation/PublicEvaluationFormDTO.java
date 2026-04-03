package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record PublicEvaluationFormDTO(
        Long stageId,
        Long sessionId,
        String etudiantNom,
        String matricule,
        String entrepriseNom,
        LocalDate dateDebut,
        LocalDate dateFin,
        List<PublicEvaluationCategoryDTO> categories) implements Serializable {
}
