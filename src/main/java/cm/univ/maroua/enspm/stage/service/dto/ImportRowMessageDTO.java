package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO ImportRowMessageDTO.
 */
public record ImportRowMessageDTO(Integer no, String matricule, String message) implements Serializable {
}
