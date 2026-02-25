package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.TypeStageService;
import cm.univ.maroua.enspm.stage.service.dto.TypeStageDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/type-stages")
public class TypeStageController {

    private final TypeStageService typeStageService;

    public TypeStageController(TypeStageService typeStageService) {
        this.typeStageService = typeStageService;
    }

    @GetMapping
    public Page<TypeStageDTO> getAllTypeStages(Pageable pageable) {
        return typeStageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeStageDTO> getTypeStage(@PathVariable Long id) {
        return typeStageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypeStageDTO> createTypeStage(@Valid @RequestBody TypeStageDTO typeStageDTO)
            throws URISyntaxException {
        if (typeStageDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        TypeStageDTO result = typeStageService.save(typeStageDTO);
        return ResponseEntity.created(new URI("/type-stages/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeStageDTO> updateTypeStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody TypeStageDTO typeStageDTO) {
        if (typeStageDTO.id() == null || !id.equals(typeStageDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        TypeStageDTO result = typeStageService.save(typeStageDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTypeStage(@PathVariable Long id) {
        typeStageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
