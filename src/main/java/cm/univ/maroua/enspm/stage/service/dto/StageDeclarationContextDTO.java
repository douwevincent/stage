package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record StageDeclarationContextDTO(
        EtudiantDTO etudiant,
        Long typeStageId,
        String typeStageLibelle,
        LocalDate dateDebut,
        LocalDate dateFin) implements Serializable {
}