package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.service.ParcoursService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/parcours")
public class ParcoursController {

    private final ParcoursService parcoursService;

    public ParcoursController(ParcoursService parcoursService) {
        this.parcoursService = parcoursService;
    }

    @GetMapping
    public Page<Parcours> getAllParcours(Pageable pageable) {
        return parcoursService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parcours> getParcours(@PathVariable Long id) {
        return parcoursService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Parcours> createParcours(@Valid @RequestBody Parcours parcours) throws URISyntaxException {
        if (parcours.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Parcours result = parcoursService.save(parcours);
        return ResponseEntity.created(new URI("/api/parcours/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parcours> updateParcours(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Parcours parcours) {
        if (parcours.getId() == null || !id.equals(parcours.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Parcours result = parcoursService.save(parcours);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcours(@PathVariable Long id) {
        parcoursService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
