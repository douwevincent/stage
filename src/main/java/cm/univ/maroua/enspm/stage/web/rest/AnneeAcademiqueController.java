package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.AnneeAcademiqueService;
import cm.univ.maroua.enspm.stage.service.dto.AnneeAcademiqueDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Controleur REST de gestion des annees academiques.
 *
 * <p>Expose des endpoints CRUD ainsi qu'un endpoint dedie a l'activation de
 * l'annee academique courante.</p>
 */
@RestController
@RequestMapping("/api/v1/annee-academiques")
public class AnneeAcademiqueController {

    private final AnneeAcademiqueService anneeAcademiqueService;

    public AnneeAcademiqueController(AnneeAcademiqueService anneeAcademiqueService) {
        this.anneeAcademiqueService = anneeAcademiqueService;
    }

    /**
     * Retourne les annees academiques de maniere paginee.
     */
    @GetMapping
    public Page<AnneeAcademiqueDTO> getAllAnneeAcademiques(Pageable pageable) {
        return anneeAcademiqueService.findAll(pageable);
    }

    /**
     * Retourne l'annee academique active.
     */
    @GetMapping("/active")
    public ResponseEntity<AnneeAcademiqueDTO> getAnneeActive() {
        return anneeAcademiqueService.findActive()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Active l'annee academique cible en desactivant les autres.
     */
    @PatchMapping("/{id}/activer")
    public ResponseEntity<AnneeAcademiqueDTO> activerAnneeAcademique(@PathVariable Long id) {
        return ResponseEntity.ok(anneeAcademiqueService.activate(id));
    }

    /**
     * Retourne une annee academique par identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnneeAcademiqueDTO> getAnneeAcademique(@PathVariable Long id) {
        return anneeAcademiqueService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cree une nouvelle annee academique.
     */
    @PostMapping
    public ResponseEntity<AnneeAcademiqueDTO> createAnneeAcademique(
            @Valid @RequestBody AnneeAcademiqueDTO anneeAcademiqueDTO)
            throws URISyntaxException {
        if (anneeAcademiqueDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        AnneeAcademiqueDTO result = anneeAcademiqueService.save(anneeAcademiqueDTO);
        return ResponseEntity.created(new URI("/annee-academiques/" + result.id()))
                .body(result);
    }

    /**
     * Met a jour une annee academique existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnneeAcademiqueDTO> updateAnneeAcademique(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody AnneeAcademiqueDTO anneeAcademiqueDTO) {
        if (anneeAcademiqueDTO.id() == null || !id.equals(anneeAcademiqueDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        AnneeAcademiqueDTO result = anneeAcademiqueService.save(anneeAcademiqueDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    /**
     * Supprime une annee academique.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnneeAcademique(@PathVariable Long id) {
        anneeAcademiqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
