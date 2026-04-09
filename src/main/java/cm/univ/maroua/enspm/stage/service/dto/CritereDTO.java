package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO CritereDTO.
 */
public record CritereDTO(Long id, String libelle, String categorie) implements Serializable {
}
