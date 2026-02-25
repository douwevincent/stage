package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.EtudiantService;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public Page<EtudiantDTO> getAllEtudiants(Pageable pageable) {
        return etudiantService.findAll(pageable);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
