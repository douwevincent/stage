package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Entreprise;
import cm.univ.maroua.enspm.stage.service.EntrepriseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @GetMapping
    public Page<Entreprise> getAllEntreprises(Pageable pageable) {
        return entrepriseService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getEntreprise(@PathVariable Long id) {
        return entrepriseService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Entreprise> createEntreprise(@Valid @RequestBody Entreprise entreprise)
            throws URISyntaxException {
        if (entreprise.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Entreprise result = entrepriseService.save(entreprise);
        return ResponseEntity.created(new URI("/api/entreprises/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entreprise> updateEntreprise(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Entreprise entreprise) {
        if (entreprise.getId() == null || !id.equals(entreprise.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Entreprise result = entrepriseService.save(entreprise);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        entrepriseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
