package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record InscriptionDTO(
                Long id,
                Long anneeAcademiqueId,
                Long etudiantId,
                Long parcoursId,
                String anneeAcademiqueLibelle,
                String etudiantMatricule,
                String etudiantNom,
                Long parcoursSpecialiteId,
                String parcoursSpecialiteCode,
                String parcoursSpecialiteIntitule,
                Long parcoursNiveauId,
                String parcoursNiveauLibelle,
                String parcoursLibelle)
                implements Serializable {
}
