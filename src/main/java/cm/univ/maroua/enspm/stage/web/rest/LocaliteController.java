package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Localite;
import cm.univ.maroua.enspm.stage.service.LocaliteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/localites")
public class LocaliteController {

    private final LocaliteService localiteService;

    public LocaliteController(LocaliteService localiteService) {
        this.localiteService = localiteService;
    }

    @GetMapping
    public Page<Localite> getAllLocalites(Pageable pageable) {
        return localiteService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Localite> getLocalite(@PathVariable Long id) {
        return localiteService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Localite> createLocalite(@Valid @RequestBody Localite localite) throws URISyntaxException {
        if (localite.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Localite result = localiteService.save(localite);
        return ResponseEntity.created(new URI("/api/localites/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Localite> updateLocalite(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Localite localite) {
        if (localite.getId() == null || !id.equals(localite.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Localite result = localiteService.save(localite);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocalite(@PathVariable Long id) {
        localiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
