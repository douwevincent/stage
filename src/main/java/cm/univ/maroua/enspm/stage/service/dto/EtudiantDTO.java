package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record EtudiantDTO(Long id, String nom, String prenom, String email, String telephone, String matricule)
        implements Serializable {
}
