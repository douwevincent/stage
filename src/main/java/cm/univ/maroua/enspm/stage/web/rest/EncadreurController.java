package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import cm.univ.maroua.enspm.stage.service.EncadreurService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/encadreurs")
public class EncadreurController {

    private final EncadreurService encadreurService;

    public EncadreurController(EncadreurService encadreurService) {
        this.encadreurService = encadreurService;
    }

    @GetMapping
    public Page<Encadreur> getAllEncadreurs(Pageable pageable) {
        return encadreurService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encadreur> getEncadreur(@PathVariable Long id) {
        return encadreurService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Encadreur> createEncadreur(@Valid @RequestBody Encadreur encadreur)
            throws URISyntaxException {
        if (encadreur.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Encadreur result = encadreurService.save(encadreur);
        return ResponseEntity.created(new URI("/api/encadreurs/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Encadreur> updateEncadreur(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Encadreur encadreur) {
        if (encadreur.getId() == null || !id.equals(encadreur.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Encadreur result = encadreurService.save(encadreur);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncadreur(@PathVariable Long id) {
        encadreurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
