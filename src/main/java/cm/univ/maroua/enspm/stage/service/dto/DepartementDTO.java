package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO DepartementDTO.
 */
public record DepartementDTO(Long id, String code, String intitule) implements Serializable {
}
