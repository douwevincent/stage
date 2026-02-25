package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.service.TypeStageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/type-stages")
public class TypeStageController {

    private final TypeStageService typeStageService;

    public TypeStageController(TypeStageService typeStageService) {
        this.typeStageService = typeStageService;
    }

    @GetMapping
    public Page<TypeStage> getAllTypeStages(Pageable pageable) {
        return typeStageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeStage> getTypeStage(@PathVariable Long id) {
        return typeStageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypeStage> createTypeStage(@Valid @RequestBody TypeStage typeStage)
            throws URISyntaxException {
        if (typeStage.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        TypeStage result = typeStageService.save(typeStage);
        return ResponseEntity.created(new URI("/api/type-stages/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeStage> updateTypeStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody TypeStage typeStage) {
        if (typeStage.getId() == null || !id.equals(typeStage.getId())) {
            return ResponseEntity.badRequest().build();
        }
        TypeStage result = typeStageService.save(typeStage);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTypeStage(@PathVariable Long id) {
        typeStageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
