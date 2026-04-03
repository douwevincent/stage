package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record PublicEvaluationNoteInputDTO(
        @NotNull Long critereId,
        @NotNull Integer valeur,
        String commentaire) implements Serializable {
}
