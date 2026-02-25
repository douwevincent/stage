package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record StageDTO(
        Long id,
        EtudiantDTO etudiant,
        LocaliteDTO localite,
        EncadreurDTO encadreur,
        TypeStageDTO typeStage,
        LocalDate dateDebut,
        LocalDate dateFin,
        AnneeAcademiqueDTO anneeAcademique,
        SessionEvaluationDTO sessionEvaluation) implements Serializable {
}
