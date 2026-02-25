package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.StageService;
import cm.univ.maroua.enspm.stage.service.dto.StageDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public Page<StageDTO> getAllStages(Pageable pageable) {
        return stageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageDTO> getStage(@PathVariable Long id) {
        return stageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StageDTO> createStage(@Valid @RequestBody StageDTO stageDTO) throws URISyntaxException {
        if (stageDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        StageDTO result = stageService.save(stageDTO);
        return ResponseEntity.created(new URI("/stages/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageDTO> updateStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody StageDTO stageDTO) {
        if (stageDTO.id() == null || !id.equals(stageDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        StageDTO result = stageService.save(stageDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long id) {
        stageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
