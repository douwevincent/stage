package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO SessionEvaluationDTO.
 */
public record SessionEvaluationDTO(Long id, String codeAcces, SessionEvaluationStatut statut, LocalDate dateLimite)
        implements Serializable {
}
