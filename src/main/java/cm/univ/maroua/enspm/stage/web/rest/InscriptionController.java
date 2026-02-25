package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.service.InscriptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @GetMapping
    public Page<Inscription> getAllInscriptions(Pageable pageable) {
        return inscriptionService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscription> getInscription(@PathVariable Long id) {
        return inscriptionService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inscription> createInscription(@Valid @RequestBody Inscription inscription)
            throws URISyntaxException {
        if (inscription.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Inscription result = inscriptionService.save(inscription);
        return ResponseEntity.created(new URI("/api/inscriptions/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inscription> updateInscription(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Inscription inscription) {
        if (inscription.getId() == null || !id.equals(inscription.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Inscription result = inscriptionService.save(inscription);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscription(@PathVariable Long id) {
        inscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
