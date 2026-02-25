package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.service.SessionEvaluationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/session-evaluations")
public class SessionEvaluationController {

    private final SessionEvaluationService sessionEvaluationService;

    public SessionEvaluationController(SessionEvaluationService sessionEvaluationService) {
        this.sessionEvaluationService = sessionEvaluationService;
    }

    @GetMapping
    public Page<SessionEvaluation> getAllSessionEvaluations(Pageable pageable) {
        return sessionEvaluationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionEvaluation> getSessionEvaluation(@PathVariable Long id) {
        return sessionEvaluationService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SessionEvaluation> createSessionEvaluation(
            @Valid @RequestBody SessionEvaluation sessionEvaluation) throws URISyntaxException {
        if (sessionEvaluation.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        SessionEvaluation result = sessionEvaluationService.save(sessionEvaluation);
        return ResponseEntity.created(new URI("/api/session-evaluations/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionEvaluation> updateSessionEvaluation(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody SessionEvaluation sessionEvaluation) {
        if (sessionEvaluation.getId() == null || !id.equals(sessionEvaluation.getId())) {
            return ResponseEntity.badRequest().build();
        }
        SessionEvaluation result = sessionEvaluationService.save(sessionEvaluation);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionEvaluation(@PathVariable Long id) {
        sessionEvaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
