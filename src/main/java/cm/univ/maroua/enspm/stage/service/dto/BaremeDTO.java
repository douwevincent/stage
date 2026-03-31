package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record BaremeDTO(Long id, String code, String libelle, Boolean actif, Boolean parDefaut) implements Serializable {
}
