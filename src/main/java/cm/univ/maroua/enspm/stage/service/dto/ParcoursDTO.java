package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record ParcoursDTO(Long id, SpecialiteDTO specialite, NiveauDTO niveau) implements Serializable {
}
