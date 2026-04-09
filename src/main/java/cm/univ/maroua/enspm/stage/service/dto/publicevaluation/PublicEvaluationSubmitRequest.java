package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * DTO d'evaluation publique PublicEvaluationSubmitRequest.
 */
public record PublicEvaluationSubmitRequest(
        @NotNull Long stageId,
        @NotEmpty List<@Valid PublicEvaluationNoteInputDTO> notes) implements Serializable {
}
