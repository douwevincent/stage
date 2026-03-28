package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.EntrepriseService;
import cm.univ.maroua.enspm.stage.service.dto.EntrepriseDTO;
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
@RequestMapping("/api/v1/entreprises")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @GetMapping
    public Page<EntrepriseDTO> getAllEntreprises(Pageable pageable) {
        return entrepriseService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrepriseDTO> getEntreprise(@PathVariable Long id) {
        return entrepriseService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntrepriseDTO> createEntreprise(@Valid @RequestBody EntrepriseDTO entrepriseDTO)
            throws URISyntaxException {
        if (entrepriseDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        EntrepriseDTO result = entrepriseService.save(entrepriseDTO);
        return ResponseEntity.created(new URI("/entreprises/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntrepriseDTO> updateEntreprise(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody EntrepriseDTO entrepriseDTO) {
        if (entrepriseDTO.id() == null || !id.equals(entrepriseDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        EntrepriseDTO result = entrepriseService.save(entrepriseDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        entrepriseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recherche")
    public List<EntrepriseDTO> rechercheParNom(@RequestParam(defaultValue = "") String q) {
        return entrepriseService.rechercheParNom(q);
    }

    @PostMapping("/recherche-ou-cree")
    public ResponseEntity<EntrepriseDTO> rechercheOuCree(@RequestBody Map<String, String> body) {
        String nom = body.getOrDefault("nom", "").trim();
        String secteur = body.getOrDefault("secteur", "");
        if (nom.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(entrepriseService.rechercheOuCree(nom, secteur));
    }
}
