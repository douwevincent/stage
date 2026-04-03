package cm.univ.maroua.enspm.stage.service.dto.publicevaluation;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;

import java.io.Serializable;

public record PublicEvaluationSubmitResponse(
        Long sessionId,
        SessionEvaluationStatut statut,
        String message) implements Serializable {
}
