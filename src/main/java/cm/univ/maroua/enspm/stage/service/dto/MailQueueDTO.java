package cm.univ.maroua.enspm.stage.service.dto;

import cm.univ.maroua.enspm.stage.domain.MailQueueStatut;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO MailQueueDTO.
 */
public record MailQueueDTO(
        Long id,
        String destinataireEmail,
        String sujet,
        String corps,
        MailQueueStatut statut,
        LocalDate datePlanifiee,
        LocalDateTime dateEnvoi,
        int nombreTentatives,
        String erreur,
        Long encadreurId,
        Long stageId,
        Long periodeStageId,
        Long notificationId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
