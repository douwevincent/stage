package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record InscriptionDTO(Long id, Long anneeAcademiqueId, Long etudiantId, Long parcoursId)
                implements Serializable {
}
