package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.SessionEvaluationService;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/session-evaluations")
public class SessionEvaluationController {

    private final SessionEvaluationService sessionEvaluationService;

    public SessionEvaluationController(SessionEvaluationService sessionEvaluationService) {
        this.sessionEvaluationService = sessionEvaluationService;
    }

    @GetMapping
    public Page<SessionEvaluationDTO> getAllSessionEvaluations(Pageable pageable) {
        return sessionEvaluationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionEvaluationDTO> getSessionEvaluation(@PathVariable Long id) {
        return sessionEvaluationService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SessionEvaluationDTO> createSessionEvaluation(
            @Valid @RequestBody SessionEvaluationDTO sessionEvaluationDTO) throws URISyntaxException {
        if (sessionEvaluationDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        SessionEvaluationDTO result = sessionEvaluationService.save(sessionEvaluationDTO);
        return ResponseEntity.created(new URI("/session-evaluations/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionEvaluationDTO> updateSessionEvaluation(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody SessionEvaluationDTO sessionEvaluationDTO) {
        if (sessionEvaluationDTO.id() == null || !id.equals(sessionEvaluationDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        SessionEvaluationDTO result = sessionEvaluationService.save(sessionEvaluationDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionEvaluation(@PathVariable Long id) {
        sessionEvaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
