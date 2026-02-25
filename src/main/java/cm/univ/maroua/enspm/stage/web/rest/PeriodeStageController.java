package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.service.PeriodeStageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/periode-stages")
public class PeriodeStageController {

    private final PeriodeStageService periodeStageService;

    public PeriodeStageController(PeriodeStageService periodeStageService) {
        this.periodeStageService = periodeStageService;
    }

    @GetMapping
    public Page<PeriodeStage> getAllPeriodeStages(Pageable pageable) {
        return periodeStageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodeStage> getPeriodeStage(@PathVariable Long id) {
        return periodeStageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PeriodeStage> createPeriodeStage(@Valid @RequestBody PeriodeStage periodeStage)
            throws URISyntaxException {
        if (periodeStage.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        PeriodeStage result = periodeStageService.save(periodeStage);
        return ResponseEntity.created(new URI("/api/periode-stages/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodeStage> updatePeriodeStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody PeriodeStage periodeStage) {
        if (periodeStage.getId() == null || !id.equals(periodeStage.getId())) {
            return ResponseEntity.badRequest().build();
        }
        PeriodeStage result = periodeStageService.save(periodeStage);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeriodeStage(@PathVariable Long id) {
        periodeStageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
