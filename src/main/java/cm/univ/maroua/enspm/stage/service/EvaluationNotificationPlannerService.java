package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.*;
import cm.univ.maroua.enspm.stage.repository.MailQueueRepository;
import cm.univ.maroua.enspm.stage.repository.NotificationRepository;
import cm.univ.maroua.enspm.stage.repository.StageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Détecte les notifications dues aujourd'hui et enfile un message en attente
 * pour chaque stage non noté concerné.
 *
 * <p>
 * Règle de déclenchement : une {@link Notification} active est due aujourd'hui
 * si
 * {@code dateReference + offsetDays == today}, où dateReference est la date
 * réelle
 * de début ou de fin du stage selon le type de notification.
 * </p>
 *
 * <p>
 * Anti-doublon : un seul message par triplet (encadreur, stage, notification)
 * grâce à la contrainte unique sur la table {@code mail_queue}.
 * </p>
 */
@Service
public class EvaluationNotificationPlannerService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationNotificationPlannerService.class);

    private static final String SUJET_DEFAUT = "Rappel : évaluation des stages en attente";
    private static final String CORPS_DEFAUT = "Bonjour,\n\nCertains stages dont vous êtes encadreur n'ont pas encore été évalués. "
            + "Merci de vous connecter à la plateforme pour saisir vos notes.\n\nCordialement.";
    private static final DateTimeFormatter DATE_FR_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE);
        private static final int EVALUATION_DELAY_DAYS_FALLBACK = 14;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final NotificationRepository notificationRepository;
    private final StageRepository stageRepository;
    private final MailQueueRepository mailQueueRepository;
    private final AppSettingService appSettingService;
    private final SessionEvaluationService sessionEvaluationService;

    public EvaluationNotificationPlannerService(
            NotificationRepository notificationRepository,
            StageRepository stageRepository,
            MailQueueRepository mailQueueRepository,
            AppSettingService appSettingService,
            SessionEvaluationService sessionEvaluationService) {
        this.notificationRepository = notificationRepository;
        this.stageRepository = stageRepository;
        this.mailQueueRepository = mailQueueRepository;
        this.appSettingService = appSettingService;
        this.sessionEvaluationService = sessionEvaluationService;
    }

    /**
     * Point d'entrée principal. Appelé par la tâche planifiée quotidienne.
     *
     * @return nombre de messages enfilés
     */
    @Transactional
    public int planifier() {
        LocalDate today = LocalDate.now();
        log.info("Planification des notifications encadreurs pour le {}", today);

        String sujet = resolveSubject();
        String corps = resolveBody();

        List<Notification> notifications = notificationRepository.findAll(Pageable.unpaged()).getContent()
                .stream()
                .filter(Notification::getActif)
                .toList();
        log.debug("J'ai eu {} notifications ", notifications.size());
        int total = 0;
        for (Notification notification : notifications) {
            total += traiterNotification(notification, today, sujet, corps);
        }

        log.info("Planification terminée : {} message(s) enfilé(s) pour le {}", total, today);
        return total;
    }

    // -----------------------------------------------------------------------
    // Logique interne
    // -----------------------------------------------------------------------

    private int traiterNotification(Notification notification, LocalDate today, String sujet, String corps) {
        Long typeStageId = notification.getTypeStage().getId();
        // DEBUT_STAGE
        // FIN_STAGE
        // JOURS_AVANT_FIN_STAGE
        // JOURS_APRES_FIN_STAGE
        

        List<Stage> stagesDues = switch (notification.getReferenceDateType()) {
            case DEBUT_STAGE -> {
                LocalDate referenceDate = today.minusDays(notification.getOffsetDays());
                log.debug("Notification {} : recherche des stages non notés pour début de stage à la date {}",
                        notification.getReferenceDateType(), referenceDate);
                yield stageRepository
                        .findStagesNonNotesPourDebutStage(typeStageId, referenceDate);
            }
            case FIN_STAGE, JOURS_AVANT_FIN_STAGE -> {                
                LocalDate referenceDate = today.plusDays(notification.getOffsetDays());
                log.debug("Notification {} : recherche des stages non notés pour fin de stage à la date {}",
                        notification.getReferenceDateType(), referenceDate);
                yield stageRepository
                        .findStagesNonNotesPourFinStage(typeStageId, referenceDate);
            }
            case JOURS_APRES_FIN_STAGE -> {
                LocalDate referenceDate = today.minusDays(notification.getOffsetDays());
                log.debug("Notification {} : recherche des stages non notés pour fin de stage à la date {}",
                        notification.getReferenceDateType(), referenceDate);
                yield stageRepository
                        .findStagesNonNotesPourFinStage(typeStageId, referenceDate);
            }
            default -> {
                log.debug("Type de référence {} non supporté pour les notifications encadreurs, ignoré.",
                        notification.getReferenceDateType());
                yield List.of();
            }
        };

        if (stagesDues.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Stage stage : stagesDues) {
            if (stage.getEncadreur() == null || stage.getEncadreur().getId() == null) {
                continue;
            }
            count += enfiler(stage, notification, today, sujet, corps);
        }

        if (count == 0) {
            log.debug("Notification {} : aucun stage éligible (déjà noté ou sans encadreur).",
                    notification.getId());
        }
        return count;
    }

    private int enfiler(Stage stage, Notification notification, LocalDate today, String sujet, String corps) {
        Encadreur encadreur = stage.getEncadreur();

        if (mailQueueRepository.existsByEncadreurIdAndStageIdAndNotificationId(
                encadreur.getId(), stage.getId(), notification.getId())) {
            log.debug("Doublon ignoré : encadreur={} stage={} notification={}",
                    encadreur.getId(), stage.getId(), notification.getId());
            return 0;
        }

        LocalDate dateLimite = resolveSessionDeadline(stage);
        SessionEvaluation session = sessionEvaluationService.ensureSessionWithCode(stage, dateLimite);

        // Intégrer le lien dans le corps du message
        String prefixeUrl = resolvePublicEvaluationUrlPrefix();
        String lien = prefixeUrl + "/evaluation-encadreur/" + session.getCodeAcces();
        String corpsAvecLien = corps.contains("{LIEN_EVALUATION}")
                ? corps.replace("{LIEN_EVALUATION}", lien)
                : corps + "\n\nLien d'évaluation : " + lien;
        corpsAvecLien = applySupportedPlaceholders(corpsAvecLien, encadreur, session.getDateLimite());

        MailQueue mail = new MailQueue();
        mail.setDestinataireEmail(encadreur.getEmail());
        mail.setSujet(sujet);
        mail.setCorps(corpsAvecLien);
        mail.setStatut(MailQueueStatut.PENDING);
        mail.setDatePlanifiee(today);
        mail.setNombreTentatives(0);
        mail.setEncadreurId(encadreur.getId());
        mail.setStageId(stage.getId());
        mail.setPeriodeStageId(null);
        mail.setNotificationId(notification.getId());

        try {
            mailQueueRepository.save(mail);
            log.debug("Mail enfilé : encadreur={} <{}> stage={} notification={} prefixe={} lien={}",
                    encadreur.getId(), encadreur.getEmail(), stage.getId(), notification.getId(), prefixeUrl, lien);
            return 1;
        } catch (DataIntegrityViolationException e) {
            // Race condition entre le check existsBy et le save : on ignore silencieusement
            log.debug("Contrainte unique sur mail_queue déclenchée (race), ignoré.");
            return 0;
        }
    }

    // -----------------------------------------------------------------------
    // Résolution du contenu depuis AppSetting (fallback sur valeur par défaut)
    // -----------------------------------------------------------------------

    private String resolveSubject() {
        try {
            String val = appSettingService.getRawValue("MAIL_SUBJECT_RAPPEL");
            return (val != null && !val.isBlank()) ? val : SUJET_DEFAUT;
        } catch (Exception e) {
            log.debug("MAIL_SUBJECT_RAPPEL non configuré, utilisation du sujet par défaut.");
            return SUJET_DEFAUT;
        }
    }

    private String resolveBody() {
        try {
            String val = appSettingService.getRawValue("MAIL_TEMPLATE_BODY");
            return (val != null && !val.isBlank()) ? val : CORPS_DEFAUT;
        } catch (Exception e) {
            log.debug("MAIL_TEMPLATE_BODY non configuré, utilisation du corps par défaut.");
            return CORPS_DEFAUT;
        }
    }

    private String resolvePublicEvaluationUrlPrefix() {
        try {
            String val = appSettingService.getRawValue("MAIL_PUBLIC_EVALUATION_URL_PREFIX");
            if (val != null && !val.isBlank()) {
                return trimTrailingSlash(val.trim());
            }
        } catch (Exception e) {
            log.debug("MAIL_PUBLIC_EVALUATION_URL_PREFIX non configuré, fallback sur app.base-url.");
        }
        return trimTrailingSlash(baseUrl);
    }

    private LocalDate resolveSessionDeadline(Stage stage) {
        if (stage == null || stage.getDateFin() == null) {
            return null;
        }
        return stage.getDateFin().plusDays(resolveEvaluationDelayDays());
    }

    private int resolveEvaluationDelayDays() {
        try {
            String val = appSettingService.getRawValue("EVALUATION_DELAY_DAYS");
            if (val == null || val.isBlank()) {
                log.debug("EVALUATION_DELAY_DAYS vide/non configure, fallback {} jours.", EVALUATION_DELAY_DAYS_FALLBACK);
                return EVALUATION_DELAY_DAYS_FALLBACK;
            }
            int delay = Integer.parseInt(val.trim());
            if (delay < 1) {
                log.debug("EVALUATION_DELAY_DAYS={} invalide, fallback {} jours.", delay,
                        EVALUATION_DELAY_DAYS_FALLBACK);
                return EVALUATION_DELAY_DAYS_FALLBACK;
            }
            return delay;
        } catch (Exception e) {
            log.debug("Impossible de resoudre EVALUATION_DELAY_DAYS, fallback {} jours.",
                    EVALUATION_DELAY_DAYS_FALLBACK);
            return EVALUATION_DELAY_DAYS_FALLBACK;
        }
    }

    private String applySupportedPlaceholders(String body, Encadreur encadreur, LocalDate dateLimite) {
        String result = body;
        if (encadreur != null && encadreur.getNom() != null) {
            result = result.replace("${encadreur.nom}", encadreur.getNom());
        }
        if (dateLimite != null) {
            result = result.replace("${stage.sessionEvaluation.dateLimite}", dateLimite.format(DATE_FR_FORMATTER));
        }
        return result;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
