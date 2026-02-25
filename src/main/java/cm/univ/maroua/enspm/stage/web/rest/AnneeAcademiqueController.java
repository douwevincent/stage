package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.service.AnneeAcademiqueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/annee-academiques")
public class AnneeAcademiqueController {

    private final AnneeAcademiqueService anneeAcademiqueService;

    public AnneeAcademiqueController(AnneeAcademiqueService anneeAcademiqueService) {
        this.anneeAcademiqueService = anneeAcademiqueService;
    }

    @GetMapping
    public Page<AnneeAcademique> getAllAnneeAcademiques(Pageable pageable) {
        return anneeAcademiqueService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnneeAcademique> getAnneeAcademique(@PathVariable Long id) {
        return anneeAcademiqueService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnneeAcademique> createAnneeAcademique(@Valid @RequestBody AnneeAcademique anneeAcademique)
            throws URISyntaxException {
        if (anneeAcademique.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        AnneeAcademique result = anneeAcademiqueService.save(anneeAcademique);
        return ResponseEntity.created(new URI("/api/annee-academiques/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnneeAcademique> updateAnneeAcademique(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody AnneeAcademique anneeAcademique) {
        if (anneeAcademique.getId() == null || !id.equals(anneeAcademique.getId())) {
            return ResponseEntity.badRequest().build();
        }
        AnneeAcademique result = anneeAcademiqueService.save(anneeAcademique);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnneeAcademique(@PathVariable Long id) {
        anneeAcademiqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
