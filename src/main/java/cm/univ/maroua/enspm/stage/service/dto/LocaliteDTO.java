package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record LocaliteDTO(Long id, String ville, String adresse, Long entrepriseId) implements Serializable {
}
