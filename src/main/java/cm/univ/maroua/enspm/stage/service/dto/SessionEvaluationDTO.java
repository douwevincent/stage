package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record SessionEvaluationDTO(Long id, String codeAcces, String statut, LocalDate dateLimite)
        implements Serializable {
}
