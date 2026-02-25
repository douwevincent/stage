package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record SpecialiteDTO(Long id, String nom, DepartementDTO departement) implements Serializable {
}
