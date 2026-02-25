package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.service.NiveauService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/niveaus")
public class NiveauController {

    private final NiveauService niveauService;

    public NiveauController(NiveauService niveauService) {
        this.niveauService = niveauService;
    }

    @GetMapping
    public Page<Niveau> getAllNiveaus(Pageable pageable) {
        return niveauService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Niveau> getNiveau(@PathVariable Long id) {
        return niveauService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Niveau> createNiveau(@Valid @RequestBody Niveau niveau) throws URISyntaxException {
        if (niveau.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Niveau result = niveauService.save(niveau);
        return ResponseEntity.created(new URI("/api/niveaus/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Niveau> updateNiveau(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Niveau niveau) {
        if (niveau.getId() == null || !id.equals(niveau.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Niveau result = niveauService.save(niveau);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNiveau(@PathVariable Long id) {
        niveauService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
