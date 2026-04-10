package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Fiche detaillee d'une evaluation de stage.
 */
public record EvaluationResultDetailDTO(
        Long stageId,
        Long sessionId,
        SessionEvaluationStatut statut,
        String etudiantNom,
        String matricule,
        String email,
        String telephone,
        String anneeAcademique,
        String departement,
        String niveau,
        String specialite,
        String entrepriseNom,
        String encadreurNom,
        LocalDate dateDebut,
        LocalDate dateFin,
        Float totalScore,
        Float maxScore,
        List<EvaluationCategoryDetailDTO> categories) implements Serializable {
}
