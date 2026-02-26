package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record SpecialiteDTO(Long id, String code, String intitule, Long departementId) implements Serializable {
}
