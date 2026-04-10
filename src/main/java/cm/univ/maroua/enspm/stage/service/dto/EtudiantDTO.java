package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO EtudiantDTO.
 */
public record EtudiantDTO(Long id, String nom, String email, String telephone, String matricule)
        implements Serializable {
}
