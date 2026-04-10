package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.SessionEvaluationService;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationResultDetailDTO;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationResultSummaryDTO;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/session-evaluations")
/**
 * Controleur REST SessionEvaluationController.
 */
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

    @GetMapping("/results")
    @PreAuthorize("isAuthenticated()")
    public Page<EvaluationResultSummaryDTO> getEvaluatedStageResults(
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) Long departementId,
            @RequestParam(required = false) Long specialiteId,
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return sessionEvaluationService.findEvaluatedWithTotals(niveauId, departementId, specialiteId, q, pageable);
    }

    @GetMapping("/{sessionId}/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EvaluationResultDetailDTO> getEvaluationDetails(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionEvaluationService.getEvaluationDetail(sessionId));
    }

    @GetMapping(value = "/{sessionId}/fiche", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadEditableSheet(@PathVariable Long sessionId) {
        byte[] pdf = sessionEvaluationService.generateEditableEvaluationPdf(sessionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=fiche-evaluation-" + sessionId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
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
