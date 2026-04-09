package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO EtudiantImportRowDTO.
 */
public record EtudiantImportRowDTO(
        Integer no,
        String matricule,
        String nom,
        String email,
        String telephone,
        String libelleNiveau,
        String codeDepartement,
        String codeSpecialite)
        implements Serializable {
}
