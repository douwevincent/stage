package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.MailQueue;
import cm.univ.maroua.enspm.stage.domain.MailQueueStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MailQueueRepository extends JpaRepository<MailQueue, Long> {

    /**
     * Retourne les messages en attente dont la date planifiée est inférieure ou égale à aujourd'hui,
     * limités par statut PENDING. Utilisé par la tâche d'envoi.
     */
    List<MailQueue> findByStatutAndDatePlanifieeLessThanEqual(MailQueueStatut statut, LocalDate date);

    Page<MailQueue> findByStatut(MailQueueStatut statut, Pageable pageable);

    long deleteByStatutAndDateEnvoiBefore(MailQueueStatut statut, LocalDateTime dateEnvoi);

        /**
         * Vérifie l'existence d'un message pour le triplet (encadreurId, stageId, notificationId).
         * Permet de tester l'idempotence avant insertion.
         */
        boolean existsByEncadreurIdAndStageIdAndNotificationId(
            Long encadreurId, Long stageId, Long notificationId);
}
