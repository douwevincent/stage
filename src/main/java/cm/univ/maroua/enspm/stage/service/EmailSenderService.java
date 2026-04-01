package cm.univ.maroua.enspm.stage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Envoie un mail simple (texte brut) via {@link JavaMailSender}.
 *
 * <p>L'adresse expéditeur est lue depuis {@code AppSettingService} (clé {@code SMTP_FROM_ADDRESS}).
 * En cas d'absence de configuration, on lève une {@link IllegalStateException} pour que
 * la tâche puisse marquer le message en FAILED.</p>
 */
@Service
public class EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender javaMailSender;
    private final AppSettingService appSettingService;

    public EmailSenderService(ObjectProvider<JavaMailSender> javaMailSenderProvider,
                              AppSettingService appSettingService) {
        this.javaMailSender = javaMailSenderProvider.getIfAvailable();
        this.appSettingService = appSettingService;
    }

    /**
     * Envoie un email en texte brut.
     *
     * @param to      adresse du destinataire
     * @param subject sujet du message
     * @param body    corps du message
     * @throws MailException            si l'envoi SMTP échoue
     * @throws IllegalStateException    si l'adresse expéditeur n'est pas configurée
     */
    public void send(String to, String subject, String body) {
        if (javaMailSender == null) {
            throw new IllegalStateException("Aucun bean JavaMailSender disponible.");
        }

        String from = appSettingService.getRawValue("SMTP_FROM_ADDRESS");
        if (from == null || from.isBlank()) {
            throw new IllegalStateException("L'adresse expéditeur SMTP_FROM_ADDRESS n'est pas configurée.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject != null ? subject : "");
        message.setText(body != null ? body : "");

        javaMailSender.send(message);
        log.info("Mail envoyé à <{}>", to);
    }
}
