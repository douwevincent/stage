package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.*;
import cm.univ.maroua.enspm.stage.repository.MailQueueRepository;
import cm.univ.maroua.enspm.stage.repository.NotificationRepository;
import cm.univ.maroua.enspm.stage.repository.PeriodeStageRepository;
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
 * pour chaque encadreur ayant au moins un stage non noté dans la période concernée.
 *
 * <p>Règle de déclenchement : une {@link Notification} active est due aujourd'hui si
 * {@code dateReference + offsetDays == today}, où dateReference est soit
 * {@code PeriodeStage.dateDebut} (DEBUT_PERIODE) soit {@code PeriodeStage.dateFin} (FIN_PERIODE).</p>
 *
 * <p>Anti-doublon : un seul message par triplet (encadreur, période, notification)
 * grâce à la contrainte unique sur la table {@code mail_queue}.</p>
 */
@Service
public class EvaluationNotificationPlannerService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationNotificationPlannerService.class);

    private static final String SUJET_DEFAUT = "Rappel : évaluation des stages en attente";
    private static final String CORPS_DEFAUT  =
            "Bonjour,\n\nCertains stages dont vous êtes encadreur n'ont pas encore été évalués. "
            + "Merci de vous connecter à la plateforme pour saisir vos notes.\n\nCordialement.";
    private static final DateTimeFormatter DATE_FR_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE);

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final NotificationRepository notificationRepository;
    private final PeriodeStageRepository periodeStageRepository;
    private final StageRepository stageRepository;
    private final MailQueueRepository mailQueueRepository;
    private final AppSettingService appSettingService;
    private final SessionEvaluationService sessionEvaluationService;

    public EvaluationNotificationPlannerService(
            NotificationRepository notificationRepository,
            PeriodeStageRepository periodeStageRepository,
            StageRepository stageRepository,
            MailQueueRepository mailQueueRepository,
            AppSettingService appSettingService,
            SessionEvaluationService sessionEvaluationService) {
        this.notificationRepository = notificationRepository;
        this.periodeStageRepository = periodeStageRepository;
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
        LocalDate referenceDate = today.minusDays(notification.getOffsetDays());

        List<PeriodeStage> periodesDues = switch (notification.getReferenceDateType()) {
            case DEBUT_PERIODE -> periodeStageRepository
                    .findByTypeStageIdAndDateDebut(typeStageId, referenceDate);
            case FIN_PERIODE   -> periodeStageRepository
                    .findByTypeStageIdAndDateFin(typeStageId, referenceDate);
            default -> {
                log.debug("Type de référence {} non supporté pour les notifications encadreurs, ignoré.",
                        notification.getReferenceDateType());
                yield List.of();
            }
        };

        if (periodesDues.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (PeriodeStage periode : periodesDues) {
            List<Encadreur> encadreurs = stageRepository.findEncadreursAvecStagesNonNotesPourPeriode(
                    typeStageId,
                    periode.getAnneeAcademique().getId(),
                    periode.getDateDebut(),
                    periode.getDateFin());

            if (encadreurs.isEmpty()) {
                continue;
            }

            for (Encadreur encadreur : encadreurs) {
                count += enfiler(encadreur, periode, notification, today, sujet, corps);
            }
        }

        if (count == 0) {
            log.debug("Notification {} : aucun encadreur éligible (tous ont noté leurs stages ou pas de stages).",
                    notification.getId());
        }
        return count;
    }

    private int enfiler(Encadreur encadreur, PeriodeStage periode, Notification notification,
                        LocalDate today, String sujet, String corps) {

        if (mailQueueRepository.existsByEncadreurIdAndPeriodeStageIdAndNotificationId(
                encadreur.getId(), periode.getId(), notification.getId())) {
            log.debug("Doublon ignoré : encadreur={} période={} notification={}",
                    encadreur.getId(), periode.getId(), notification.getId());
            return 0;
        }

        // Créer ou récupérer une SessionEvaluation avec code court pour chaque stage éligible
        List<Stage> stages = stageRepository.findStagesNonNotesPourEncadreurEtPeriode(
                encadreur.getId(),
                periode.getAnneeAcademique().getId(),
                periode.getDateDebut(),
                periode.getDateFin());

        if (stages.isEmpty()) {
            return 0;
        }

        String premierCode = null;
        LocalDate premiereDateLimite = null;
        for (Stage stage : stages) {
            SessionEvaluation session = sessionEvaluationService.ensureSessionWithCode(stage, periode.getDateFin());
            if (premierCode == null) {
                premierCode = session.getCodeAcces();
                premiereDateLimite = session.getDateLimite();
            }
        }

        // Intégrer le lien dans le corps du message
        String prefixeUrl = resolvePublicEvaluationUrlPrefix();
        String lien = prefixeUrl + "/evaluation-encadreur/" + premierCode;
        String corpsAvecLien = corps.contains("{LIEN_EVALUATION}")
                ? corps.replace("{LIEN_EVALUATION}", lien)
                : corps + "\n\nLien d'évaluation : " + lien;
        corpsAvecLien = applySupportedPlaceholders(corpsAvecLien, encadreur, premiereDateLimite);

        MailQueue mail = new MailQueue();
        mail.setDestinataireEmail(encadreur.getEmail());
        mail.setSujet(sujet);
        mail.setCorps(corpsAvecLien);
        mail.setStatut(MailQueueStatut.PENDING);
        mail.setDatePlanifiee(today);
        mail.setNombreTentatives(0);
        mail.setEncadreurId(encadreur.getId());
        mail.setPeriodeStageId(periode.getId());
        mail.setNotificationId(notification.getId());

        try {
            mailQueueRepository.save(mail);
            log.debug("Mail enfilé : encadreur={} <{}> période={} notification={} prefixe={} lien={}",
                    encadreur.getId(), encadreur.getEmail(), periode.getId(), notification.getId(), prefixeUrl, lien);
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
