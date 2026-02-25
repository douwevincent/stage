package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Critere;
import cm.univ.maroua.enspm.stage.service.CritereService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/criteres")
public class CritereController {

    private final CritereService critereService;

    public CritereController(CritereService critereService) {
        this.critereService = critereService;
    }

    @GetMapping
    public Page<Critere> getAllCriteres(Pageable pageable) {
        return critereService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Critere> getCritere(@PathVariable Long id) {
        return critereService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Critere> createCritere(@Valid @RequestBody Critere critere) throws URISyntaxException {
        if (critere.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Critere result = critereService.save(critere);
        return ResponseEntity.created(new URI("/api/criteres/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Critere> updateCritere(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Critere critere) {
        if (critere.getId() == null || !id.equals(critere.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Critere result = critereService.save(critere);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCritere(@PathVariable Long id) {
        critereService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
