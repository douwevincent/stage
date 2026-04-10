package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO EncadreurDTO.
 */
public record EncadreurDTO(Long id, String nom, String telephone, String email, Long entrepriseId)
                implements Serializable {
}
