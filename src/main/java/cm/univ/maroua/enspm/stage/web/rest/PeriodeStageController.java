package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.PeriodeStageService;
import cm.univ.maroua.enspm.stage.service.dto.PeriodeStageDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/periode-stages")
public class PeriodeStageController {

    private final PeriodeStageService periodeStageService;

    public PeriodeStageController(PeriodeStageService periodeStageService) {
        this.periodeStageService = periodeStageService;
    }

    @GetMapping
    public Page<PeriodeStageDTO> getAllPeriodeStages(Pageable pageable) {
        return periodeStageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodeStageDTO> getPeriodeStage(@PathVariable Long id) {
        return periodeStageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PeriodeStageDTO> createPeriodeStage(@Valid @RequestBody PeriodeStageDTO periodeStageDTO)
            throws URISyntaxException {
        if (periodeStageDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        PeriodeStageDTO result = periodeStageService.save(periodeStageDTO);
        return ResponseEntity.created(new URI("/periode-stages/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeriodeStageDTO> updatePeriodeStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody PeriodeStageDTO periodeStageDTO) {
        if (periodeStageDTO.id() == null || !id.equals(periodeStageDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        PeriodeStageDTO result = periodeStageService.save(periodeStageDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePeriodeStage(@PathVariable Long id) {
        periodeStageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
