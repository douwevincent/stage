package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record AnneeAcademiqueDTO(Long id, String libelle, boolean actif) implements Serializable {
}
