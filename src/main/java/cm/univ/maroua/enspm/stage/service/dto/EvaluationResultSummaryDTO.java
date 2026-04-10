package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Synthese d'evaluation de stage sans detail des notes individuelles.
 */
public record EvaluationResultSummaryDTO(
        Long stageId,
        Long sessionId,
        SessionEvaluationStatut statut,
        String etudiantNom,
        String matricule,
        String departement,
        String niveau,
        String specialite,
        String entrepriseNom,
        LocalDate dateDebut,
        LocalDate dateFin,
        Float totalScore,
        Float maxScore) implements Serializable {
}
