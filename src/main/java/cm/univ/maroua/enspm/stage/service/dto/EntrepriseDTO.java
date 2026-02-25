package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record EntrepriseDTO(Long id, String nom, String secteur) implements Serializable {
}
