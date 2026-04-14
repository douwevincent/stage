package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.MailQueueStatut;
import cm.univ.maroua.enspm.stage.service.MailQueueService;
import cm.univ.maroua.enspm.stage.service.dto.MailQueueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/mail-queue")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
/**
 * Controleur REST MailQueueAdminController.
 */
public class MailQueueAdminController {

    private final MailQueueService mailQueueService;

    public MailQueueAdminController(MailQueueService mailQueueService) {
        this.mailQueueService = mailQueueService;
    }

    @GetMapping
    public Page<MailQueueDTO> list(
            @RequestParam(value = "statut", required = false) MailQueueStatut statut,
            Pageable pageable) {
        return mailQueueService.findAll(statut, pageable);
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<MailQueueDTO> retryOne(@PathVariable Long id) {
        return ResponseEntity.ok(mailQueueService.retry(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        mailQueueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/retry-failed")
    public ResponseEntity<Map<String, Integer>> retryFailed(
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return ResponseEntity.ok(mailQueueService.retryFailedBatch(limit));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Long>> cleanupOld(
            @RequestParam(value = "statut", required = false) MailQueueStatut statut,
            @RequestParam(value = "olderThanDays", defaultValue = "30") int olderThanDays) {
        return ResponseEntity.ok(mailQueueService.deleteOld(statut, olderThanDays));
    }
}
