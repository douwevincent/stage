package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.service.SpecialiteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/specialites")
public class SpecialiteController {

    private final SpecialiteService specialiteService;

    public SpecialiteController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public Page<Specialite> getAllSpecialites(Pageable pageable) {
        return specialiteService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialite> getSpecialite(@PathVariable Long id) {
        return specialiteService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Specialite> createSpecialite(@Valid @RequestBody Specialite specialite)
            throws URISyntaxException {
        if (specialite.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Specialite result = specialiteService.save(specialite);
        return ResponseEntity.created(new URI("/api/specialites/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Specialite> updateSpecialite(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Specialite specialite) {
        if (specialite.getId() == null || !id.equals(specialite.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Specialite result = specialiteService.save(specialite);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialite(@PathVariable Long id) {
        specialiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
