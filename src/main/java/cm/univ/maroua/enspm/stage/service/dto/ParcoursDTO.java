package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record ParcoursDTO(Long id, Long specialiteId, Long niveauId) implements Serializable {
}
