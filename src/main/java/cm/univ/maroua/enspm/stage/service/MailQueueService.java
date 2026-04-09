package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.MailQueue;
import cm.univ.maroua.enspm.stage.domain.MailQueueStatut;
import cm.univ.maroua.enspm.stage.repository.MailQueueRepository;
import cm.univ.maroua.enspm.stage.service.dto.MailQueueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
/**
 * Service metier MailQueueService.
 */
public class MailQueueService {

    private final MailQueueRepository mailQueueRepository;

    public MailQueueService(MailQueueRepository mailQueueRepository) {
        this.mailQueueRepository = mailQueueRepository;
    }

    @Transactional(readOnly = true)
    public Page<MailQueueDTO> findAll(MailQueueStatut statut, Pageable pageable) {
        if (statut == null) {
            return mailQueueRepository.findAll(pageable).map(this::toDto);
        }
        return mailQueueRepository.findByStatut(statut, pageable).map(this::toDto);
    }

    public MailQueueDTO retry(Long id) {
        MailQueue mail = mailQueueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable: " + id));

        // Relance manuelle explicite: on remet en attente et on réinitialise les infos d'échec.
        mail.setStatut(MailQueueStatut.PENDING);
        mail.setErreur(null);
        mail.setDateEnvoi(null);
        mail.setDatePlanifiee(LocalDate.now());
        mail.setNombreTentatives(0);

        return toDto(mailQueueRepository.save(mail));
    }

    public Map<String, Integer> retryFailedBatch(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        List<MailQueue> failed = mailQueueRepository
                .findByStatut(MailQueueStatut.FAILED, Pageable.ofSize(safeLimit))
                .getContent();

        failed.forEach(mail -> {
            mail.setStatut(MailQueueStatut.PENDING);
            mail.setErreur(null);
            mail.setDateEnvoi(null);
            mail.setDatePlanifiee(LocalDate.now());
            mail.setNombreTentatives(0);
        });

        mailQueueRepository.saveAll(failed);

        return Map.of(
                "retried", failed.size(),
                "limit", safeLimit
        );
    }

    public Map<String, Long> deleteOld(MailQueueStatut statut, int olderThanDays) {
        MailQueueStatut targetStatut = (statut != null) ? statut : MailQueueStatut.SENT;
        int safeDays = Math.max(1, Math.min(olderThanDays, 3650));
        LocalDateTime cutoff = LocalDateTime.now().minusDays(safeDays);

        long deleted = mailQueueRepository.deleteByStatutAndDateEnvoiBefore(targetStatut, cutoff);

        return Map.of(
                "deleted", deleted,
                "olderThanDays", (long) safeDays
        );
    }

    private MailQueueDTO toDto(MailQueue mail) {
        return new MailQueueDTO(
                mail.getId(),
                mail.getDestinataireEmail(),
                mail.getSujet(),
                mail.getCorps(),
                mail.getStatut(),
                mail.getDatePlanifiee(),
                mail.getDateEnvoi(),
                mail.getNombreTentatives(),
                mail.getErreur(),
                mail.getEncadreurId(),
                mail.getPeriodeStageId(),
                mail.getNotificationId(),
                mail.getCreatedAt(),
                mail.getUpdatedAt()
        );
    }
}
