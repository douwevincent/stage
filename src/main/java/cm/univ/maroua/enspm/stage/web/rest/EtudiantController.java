package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.EtudiantService;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantDTO;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantImportResultDTO;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantImportRowDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/etudiants")
/**
 * Controleur REST EtudiantController.
 */
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public Page<EtudiantDTO> getAllEtudiants(Pageable pageable) {
        return etudiantService.findAll(pageable);
    }

    @GetMapping("/recherche")
    public Page<EtudiantDTO> rechercheEtudiants(
            @RequestParam(defaultValue = "") String q,
            Pageable pageable) {
        return etudiantService.search(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDTO> getEtudiant(@PathVariable Long id) {
        return etudiantService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EtudiantDTO> createEtudiant(@Valid @RequestBody EtudiantDTO etudiantDTO)
            throws URISyntaxException {
        if (etudiantDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        EtudiantDTO result = etudiantService.save(etudiantDTO);
        return ResponseEntity.created(new URI("/etudiants/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EtudiantDTO> updateEtudiant(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody EtudiantDTO etudiantDTO) {
        if (etudiantDTO.id() == null || !id.equals(etudiantDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        EtudiantDTO result = etudiantService.save(etudiantDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importEtudiants(@RequestBody List<EtudiantImportRowDTO> rows) {
        try {
            EtudiantImportResultDTO result = etudiantService.importRows(rows);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/validate-matricule/{matricule}")
    public ResponseEntity<EtudiantDTO> validateMatricule(@PathVariable String matricule) {
        return etudiantService.findByMatricule(matricule)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/validate-matricule/{matricule}/declaration-context")
    public ResponseEntity<?> getStageDeclarationContext(@PathVariable String matricule) {
        try {
            return etudiantService.findStageDeclarationContext(matricule)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
