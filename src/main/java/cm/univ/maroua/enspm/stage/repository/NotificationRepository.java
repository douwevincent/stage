package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository JPA NotificationRepository.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByTypeStageId(Long typeStageId, Pageable pageable);
}
