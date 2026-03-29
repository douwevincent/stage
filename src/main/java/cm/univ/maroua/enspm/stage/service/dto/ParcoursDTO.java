package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record ParcoursDTO(
				Long id,
				Long departementId,
				String departementCode,
				String departementIntitule,
				Long specialiteId,
				Long niveauId,
				Long baremeId,
				String baremeCode,
				String specialiteCode,
				String specialiteIntitule,
				String niveauLibelle,
				String libelle) implements Serializable {
}
