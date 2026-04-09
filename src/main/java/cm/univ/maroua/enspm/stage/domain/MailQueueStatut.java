package cm.univ.maroua.enspm.stage.domain;

/**
 * Etats possibles d'un message dans la file d'envoi de mails.
 */
public enum MailQueueStatut {
    PENDING,
    SENT,
    FAILED
}
