package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.service.StageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public Page<Stage> getAllStages(Pageable pageable) {
        return stageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stage> getStage(@PathVariable Long id) {
        return stageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Stage> createStage(@Valid @RequestBody Stage stage) throws URISyntaxException {
        if (stage.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Stage result = stageService.save(stage);
        return ResponseEntity.created(new URI("/api/stages/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stage> updateStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Stage stage) {
        if (stage.getId() == null || !id.equals(stage.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Stage result = stageService.save(stage);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long id) {
        stageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
