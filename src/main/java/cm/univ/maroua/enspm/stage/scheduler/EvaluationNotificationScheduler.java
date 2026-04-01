package cm.univ.maroua.enspm.stage.scheduler;

import cm.univ.maroua.enspm.stage.domain.MailQueue;
import cm.univ.maroua.enspm.stage.domain.MailQueueStatut;
import cm.univ.maroua.enspm.stage.repository.MailQueueRepository;
import cm.univ.maroua.enspm.stage.service.EmailSenderService;
import cm.univ.maroua.enspm.stage.service.EvaluationNotificationPlannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tâches planifiées pour la gestion des notifications encadreurs.
 *
 * <p>Deux tâches indépendantes :</p>
 * <ol>
 *   <li>{@code planifier} — détecte les notifications dues aujourd'hui et peuple la file de mails.</li>
 *   <li>{@code envoyerMailsEnAttente} — consomme les messages PENDING et les envoie.</li>
 * </ol>
 *
 * <p>Les expressions cron sont configurables via {@code application.properties} :</p>
 * <pre>
 *   app.notification.cron.planifier=0 0 7 * * *   # chaque jour à 07h00
 *   app.notification.cron.envoi=0 0 8 * * *        # chaque jour à 08h00
 *   app.notification.max-tentatives=3
 * </pre>
 */
@Component
public class EvaluationNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EvaluationNotificationScheduler.class);

    @Value("${app.notification.max-tentatives:3}")
    private int maxTentatives;

    private final EvaluationNotificationPlannerService plannerService;
    private final MailQueueRepository mailQueueRepository;
    private final EmailSenderService emailSenderService;

    public EvaluationNotificationScheduler(
            EvaluationNotificationPlannerService plannerService,
            MailQueueRepository mailQueueRepository,
            EmailSenderService emailSenderService) {
        this.plannerService = plannerService;
        this.mailQueueRepository = mailQueueRepository;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Tâche de planification : détecte les notifications dues aujourd'hui
     * et enfile les messages PENDING pour chaque encadreur éligible.
     */
    @Scheduled(cron = "${app.notification.cron.planifier:0 0 7 * * *}")
    public void planifier() {
        log.info("=== Début tâche : planification des notifications encadreurs ===");
        try {
            int nbEnfiles = plannerService.planifier();
            log.info("=== Fin tâche planification : {} message(s) enfilé(s) ===", nbEnfiles);
        } catch (Exception e) {
            log.error("Erreur lors de la planification des notifications encadreurs", e);
        }
    }

    /**
     * Tâche d'envoi : consomme les messages PENDING et les envoie un par un.
     * Les messages échoués sont marqués FAILED ; après {@code maxTentatives} échecs ils ne sont plus retraités.
     */
    @Scheduled(cron = "${app.notification.cron.envoi:0 0 8 * * *}")
    @Transactional
    public void envoyerMailsEnAttente() {
        log.info("=== Début tâche : envoi des mails en attente ===");

        List<MailQueue> pendingMails = mailQueueRepository
                .findByStatutAndDatePlanifieeLessThanEqual(MailQueueStatut.PENDING, LocalDate.now());

        int envoyes = 0;
        int echoues = 0;

        for (MailQueue mail : pendingMails) {
            if (mail.getNombreTentatives() >= maxTentatives) {
                log.warn("Mail {} abandonné après {} tentatives (destinataire: {})",
                        mail.getId(), mail.getNombreTentatives(), mail.getDestinataireEmail());
                mail.setStatut(MailQueueStatut.FAILED);
                mail.setErreur("Nombre maximum de tentatives atteint (" + maxTentatives + ")");
                mailQueueRepository.save(mail);
                echoues++;
                continue;
            }

            try {
                emailSenderService.send(
                        mail.getDestinataireEmail(),
                        mail.getSujet(),
                        mail.getCorps());

                mail.setStatut(MailQueueStatut.SENT);
                mail.setDateEnvoi(LocalDateTime.now());
                mail.setErreur(null);
                mailQueueRepository.save(mail);
                envoyes++;

            } catch (Exception e) {
                mail.setNombreTentatives(mail.getNombreTentatives() + 1);
                mail.setErreur(e.getMessage());
                if (mail.getNombreTentatives() >= maxTentatives) {
                    mail.setStatut(MailQueueStatut.FAILED);
                    log.error("Mail {} en FAILED après {} tentatives : {}", mail.getId(),
                            mail.getNombreTentatives(), e.getMessage());
                } else {
                    log.warn("Échec envoi mail {} (tentative {}/{}) : {}",
                            mail.getId(), mail.getNombreTentatives(), maxTentatives, e.getMessage());
                }
                mailQueueRepository.save(mail);
                echoues++;
            }
        }

        log.info("=== Fin tâche envoi : {} envoyé(s), {} échoué(s) sur {} en attente ===",
                envoyes, echoues, pendingMails.size());
    }
}
