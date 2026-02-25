package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Etudiant;
import cm.univ.maroua.enspm.stage.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public Page<Etudiant> getAllEtudiants(Pageable pageable) {
        return etudiantService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiant(@PathVariable Long id) {
        return etudiantService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Etudiant> createEtudiant(@Valid @RequestBody Etudiant etudiant) throws URISyntaxException {
        if (etudiant.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Etudiant result = etudiantService.save(etudiant);
        return ResponseEntity.created(new URI("/api/etudiants/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Etudiant etudiant) {
        if (etudiant.getId() == null || !id.equals(etudiant.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Etudiant result = etudiantService.save(etudiant);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
