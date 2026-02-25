package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.CritereService;
import cm.univ.maroua.enspm.stage.service.dto.CritereDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/criteres")
public class CritereController {

    private final CritereService critereService;

    public CritereController(CritereService critereService) {
        this.critereService = critereService;
    }

    @GetMapping
    public Page<CritereDTO> getAllCriteres(Pageable pageable) {
        return critereService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CritereDTO> getCritere(@PathVariable Long id) {
        return critereService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CritereDTO> createCritere(@Valid @RequestBody CritereDTO critereDTO)
            throws URISyntaxException {
        if (critereDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        CritereDTO result = critereService.save(critereDTO);
        return ResponseEntity.created(new URI("/criteres/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CritereDTO> updateCritere(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody CritereDTO critereDTO) {
        if (critereDTO.id() == null || !id.equals(critereDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        CritereDTO result = critereService.save(critereDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCritere(@PathVariable Long id) {
        critereService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
