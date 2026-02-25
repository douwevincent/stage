package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Departement;
import cm.univ.maroua.enspm.stage.service.DepartementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/departements")
public class DepartementController {

    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @GetMapping
    public Page<Departement> getAllDepartements(Pageable pageable) {
        return departementService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartement(@PathVariable Long id) {
        return departementService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Departement> createDepartement(@Valid @RequestBody Departement departement)
            throws URISyntaxException {
        if (departement.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Departement result = departementService.save(departement);
        return ResponseEntity.created(new URI("/api/departements/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departement> updateDepartement(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Departement departement) {
        if (departement.getId() == null || !id.equals(departement.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Departement result = departementService.save(departement);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        departementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
