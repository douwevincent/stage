package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.CritereParcoursService;
import cm.univ.maroua.enspm.stage.service.dto.CritereParcoursDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/critere-parcours")
public class CritereParcoursController {

    private final CritereParcoursService critereParcoursService;

    public CritereParcoursController(CritereParcoursService critereParcoursService) {
        this.critereParcoursService = critereParcoursService;
    }

    @GetMapping
    public Page<CritereParcoursDTO> getAllCritereParcours(Pageable pageable) {
        return critereParcoursService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CritereParcoursDTO> getCritereParcours(@PathVariable Long id) {
        return critereParcoursService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CritereParcoursDTO> createCritereParcours(
            @Valid @RequestBody CritereParcoursDTO critereParcoursDTO)
            throws URISyntaxException {
        if (critereParcoursDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        CritereParcoursDTO result = critereParcoursService.save(critereParcoursDTO);
        return ResponseEntity.created(new URI("/critere-parcours/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CritereParcoursDTO> updateCritereParcours(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody CritereParcoursDTO critereParcoursDTO) {
        if (critereParcoursDTO.id() == null || !id.equals(critereParcoursDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        CritereParcoursDTO result = critereParcoursService.save(critereParcoursDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCritereParcours(@PathVariable Long id) {
        critereParcoursService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
