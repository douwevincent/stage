package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.NotificationService;
import cm.univ.maroua.enspm.stage.service.dto.NotificationDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Page<NotificationDTO> getAllNotifications(
            @RequestParam(value = "typeStageId", required = false) Long typeStageId,
            Pageable pageable) {
        if (typeStageId != null) {
            return notificationService.findAllByTypeStageId(typeStageId, pageable);
        }
        return notificationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long id) {
        return notificationService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO)
            throws URISyntaxException {
        if (notificationDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        NotificationDTO result = notificationService.save(notificationDTO);
        return ResponseEntity.created(new URI("/api/v1/notifications/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody NotificationDTO notificationDTO) {
        if (notificationDTO.id() == null || !id.equals(notificationDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        NotificationDTO result = notificationService.save(notificationDTO);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
