package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.PublicEvaluationService;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationFormDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationStageItemDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationSubmitRequest;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationSubmitResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/evaluations")
/**
 * Controleur REST PublicEvaluationController.
 */
public class PublicEvaluationController {

    private final PublicEvaluationService publicEvaluationService;

    public PublicEvaluationController(PublicEvaluationService publicEvaluationService) {
        this.publicEvaluationService = publicEvaluationService;
    }

    @GetMapping("/{codeAcces}/stages")
    public ResponseEntity<List<PublicEvaluationStageItemDTO>> getStages(@PathVariable String codeAcces) {
        return ResponseEntity.ok(publicEvaluationService.getStagesByAccessCode(codeAcces));
    }

    @GetMapping("/{codeAcces}/stages/{stageId}/form")
    public ResponseEntity<PublicEvaluationFormDTO> getEvaluationForm(
            @PathVariable String codeAcces,
            @PathVariable Long stageId) {
        return ResponseEntity.ok(publicEvaluationService.getEvaluationForm(codeAcces, stageId));
    }

    @PostMapping("/{codeAcces}/submit")
    public ResponseEntity<PublicEvaluationSubmitResponse> submitEvaluation(
            @PathVariable String codeAcces,
            @Valid @RequestBody PublicEvaluationSubmitRequest request) {
        return ResponseEntity.ok(publicEvaluationService.submit(codeAcces, request));
    }
}
