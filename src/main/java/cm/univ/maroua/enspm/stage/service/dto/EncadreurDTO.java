package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record EncadreurDTO(Long id, String nom, String prenom, String email, Long entrepriseId)
                implements Serializable {
}
