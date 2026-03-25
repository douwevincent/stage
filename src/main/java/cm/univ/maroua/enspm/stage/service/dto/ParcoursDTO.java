package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record ParcoursDTO(
				Long id,
				Long specialiteId,
				Long niveauId,
				String specialiteCode,
				String specialiteIntitule,
				String niveauLibelle,
				String libelle) implements Serializable {
}
