package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "mail_queue",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_mail_queue_encadreur_periode_notif",
            columnNames = {"encadreur_id", "periode_stage_id", "notification_id"}
        )
    }
)
public class MailQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destinataireEmail;

    private String sujet;

    @Column(columnDefinition = "TEXT")
    private String corps;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MailQueueStatut statut = MailQueueStatut.PENDING;

    /** Date à laquelle le message a été enfilé (date de déclenchement calculée). */
    @Column(nullable = false)
    private LocalDate datePlanifiee;

    /** Date d'envoi effectif, null tant que non envoyé. */
    private LocalDateTime dateEnvoi;

    @Column(nullable = false)
    private int nombreTentatives = 0;

    @Column(columnDefinition = "TEXT")
    private String erreur;

    // Références métier pour idempotence et traçabilité
    @Column(name = "encadreur_id", nullable = false)
    private Long encadreurId;

    @Column(name = "periode_stage_id", nullable = false)
    private Long periodeStageId;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
