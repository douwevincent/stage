package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.LocaliteService;
import cm.univ.maroua.enspm.stage.service.dto.LocaliteDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/localites")
public class LocaliteController {

    private final LocaliteService localiteService;

    public LocaliteController(LocaliteService localiteService) {
        this.localiteService = localiteService;
    }

    @GetMapping
    public Page<LocaliteDTO> getAllLocalites(Pageable pageable) {
        return localiteService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocaliteDTO> getLocalite(@PathVariable Long id) {
        return localiteService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LocaliteDTO> createLocalite(@Valid @RequestBody LocaliteDTO localiteDTO)
            throws URISyntaxException {
        if (localiteDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        LocaliteDTO result = localiteService.save(localiteDTO);
        return ResponseEntity.created(new URI("/localites/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocaliteDTO> updateLocalite(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody LocaliteDTO localiteDTO) {
        if (localiteDTO.id() == null || !id.equals(localiteDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        LocaliteDTO result = localiteService.save(localiteDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocalite(@PathVariable Long id) {
        localiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
