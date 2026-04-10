package cm.univ.maroua.enspm.stage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Properties;

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

    private final AppSettingService appSettingService;

    public EmailSenderService(AppSettingService appSettingService) {
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
        JavaMailSender javaMailSender = buildJavaMailSender();

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

    private JavaMailSender buildJavaMailSender() {
        String host = requiredSetting("SMTP_HOST");
        int port = parseInt(requiredSetting("SMTP_PORT"), "SMTP_PORT doit être un entier.");
        String securityMode = requiredSetting("SMTP_SECURITY_MODE").trim().toUpperCase(Locale.ROOT);
        boolean authEnabled = parseBoolean(requiredSetting("SMTP_AUTH_ENABLED"),
                "SMTP_AUTH_ENABLED doit valoir true ou false.");

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);

        if (authEnabled) {
            sender.setUsername(requiredSetting("SMTP_USERNAME"));
            sender.setPassword(requiredSetting("SMTP_PASSWORD"));
        }

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(authEnabled));
        props.put("mail.smtp.connectiontimeout", requiredSetting("SMTP_CONNECTION_TIMEOUT_MS"));
        props.put("mail.smtp.timeout", requiredSetting("SMTP_TIMEOUT_MS"));
        props.put("mail.smtp.writetimeout", requiredSetting("SMTP_WRITE_TIMEOUT_MS"));

        String mechanisms = appSettingService.getRawValue("SMTP_AUTH_MECHANISMS");
        if (mechanisms != null && !mechanisms.isBlank()) {
            props.put("mail.smtp.auth.mechanisms", mechanisms.trim());
        }

        String sslTrust = appSettingService.getRawValue("SMTP_SSL_TRUST");
        if (sslTrust != null && !sslTrust.isBlank()) {
            props.put("mail.smtp.ssl.trust", sslTrust.trim());
        }

        switch (securityMode) {
            case "NONE" -> {
                props.put("mail.smtp.starttls.enable", "false");
                props.put("mail.smtp.ssl.enable", "false");
            }
            case "STARTTLS" -> {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.ssl.enable", "false");
            }
            case "SSL_TLS" -> {
                props.put("mail.smtp.starttls.enable", "false");
                props.put("mail.smtp.ssl.enable", "true");
            }
            default -> throw new IllegalStateException(
                    "Valeur invalide pour SMTP_SECURITY_MODE: " + securityMode);
        }

        return sender;
    }

    private String requiredSetting(String key) {
        String value = appSettingService.getRawValue(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Le paramètre " + key + " est requis pour l'envoi de mails.");
        }
        return value.trim();
    }

    private int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(errorMessage, ex);
        }
    }

    private boolean parseBoolean(String value, String errorMessage) {
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new IllegalStateException(errorMessage);
        }
        return Boolean.parseBoolean(value);
    }
}
