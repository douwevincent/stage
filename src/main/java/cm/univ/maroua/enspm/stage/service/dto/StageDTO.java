package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.Source;
import cm.univ.maroua.enspm.stage.domain.Statut;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO StageDTO.
 */
public record StageDTO(
                Long id,
                Long etudiantId,
                String etudiantMatricule,
                String etudiantNom,
                Long typeStageId,
                String typeStageLibelle,
                Long entrepriseId,
                String entrepriseNom,
                String ville,
                String adresse,
                Long encadreurId,
                String encadreurNom,
                LocalDate dateDebut,
                LocalDate dateFin,
                Long anneeAcademiqueId,
                Long sessionEvaluationId,
                Source source,
                Statut statut,
                String cheminAutorisation) implements Serializable {
}
