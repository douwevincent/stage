package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record InscriptionDTO(Long id, AnneeAcademiqueDTO anneeAcademique, EtudiantDTO etudiant, ParcoursDTO parcours)
        implements Serializable {
}
