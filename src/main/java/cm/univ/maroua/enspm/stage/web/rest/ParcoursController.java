package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.ParcoursService;
import cm.univ.maroua.enspm.stage.service.dto.ParcoursDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/parcours")
public class ParcoursController {

    private final ParcoursService parcoursService;

    public ParcoursController(ParcoursService parcoursService) {
        this.parcoursService = parcoursService;
    }

    @GetMapping
    public Page<ParcoursDTO> getAllParcours(Pageable pageable) {
        return parcoursService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcoursDTO> getParcours(@PathVariable Long id) {
        return parcoursService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ParcoursDTO> createParcours(@Valid @RequestBody ParcoursDTO parcoursDTO)
            throws URISyntaxException {
        if (parcoursDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        ParcoursDTO result = parcoursService.save(parcoursDTO);
        return ResponseEntity.created(new URI("/parcours/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParcoursDTO> updateParcours(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody ParcoursDTO parcoursDTO) {
        if (parcoursDTO.id() == null || !id.equals(parcoursDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        ParcoursDTO result = parcoursService.save(parcoursDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcours(@PathVariable Long id) {
        parcoursService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
