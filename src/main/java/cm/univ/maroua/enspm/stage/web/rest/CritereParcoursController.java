package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import cm.univ.maroua.enspm.stage.service.CritereParcoursService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/critere-parcours")
public class CritereParcoursController {

    private final CritereParcoursService critereParcoursService;

    public CritereParcoursController(CritereParcoursService critereParcoursService) {
        this.critereParcoursService = critereParcoursService;
    }

    @GetMapping
    public Page<CritereParcours> getAllCritereParcours(Pageable pageable) {
        return critereParcoursService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CritereParcours> getCritereParcours(@PathVariable Long id) {
        return critereParcoursService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CritereParcours> createCritereParcours(@Valid @RequestBody CritereParcours critereParcours)
            throws URISyntaxException {
        if (critereParcours.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        CritereParcours result = critereParcoursService.save(critereParcours);
        return ResponseEntity.created(new URI("/api/critere-parcours/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CritereParcours> updateCritereParcours(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody CritereParcours critereParcours) {
        if (critereParcours.getId() == null || !id.equals(critereParcours.getId())) {
            return ResponseEntity.badRequest().build();
        }
        CritereParcours result = critereParcoursService.save(critereParcours);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCritereParcours(@PathVariable Long id) {
        critereParcoursService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
