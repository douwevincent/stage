package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.util.List;

public record EtudiantImportResultDTO(
        int totalLignes,
        int etudiantsCrees,
        int etudiantsExistants,
        int inscriptionsCreees,
        int avertissements,
        int erreurs,
        List<ImportRowMessageDTO> detailsErreurs,
        List<ImportRowMessageDTO> detailsAvertissements)
        implements Serializable {
}
