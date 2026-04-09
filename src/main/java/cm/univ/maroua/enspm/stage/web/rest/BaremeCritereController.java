package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.BaremeCritereService;
import cm.univ.maroua.enspm.stage.service.dto.BaremeCritereDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/bareme-criteres")
/**
 * Controleur REST BaremeCritereController.
 */
public class BaremeCritereController {

    private final BaremeCritereService baremeCritereService;

    public BaremeCritereController(BaremeCritereService baremeCritereService) {
        this.baremeCritereService = baremeCritereService;
    }

    @GetMapping
    public Page<BaremeCritereDTO> getAllBaremeCriteres(Pageable pageable,
            @RequestParam(name = "baremeId", required = false) Long baremeId) {
        return baremeCritereService.findAll(pageable, baremeId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaremeCritereDTO> getBaremeCritere(@PathVariable Long id) {
        return baremeCritereService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BaremeCritereDTO> createBaremeCritere(@Valid @RequestBody BaremeCritereDTO baremeCritereDTO)
            throws URISyntaxException {
        if (baremeCritereDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        BaremeCritereDTO result = baremeCritereService.save(baremeCritereDTO);
        return ResponseEntity.created(new URI("/bareme-criteres/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaremeCritereDTO> updateBaremeCritere(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody BaremeCritereDTO baremeCritereDTO) {
        if (baremeCritereDTO.id() == null || !id.equals(baremeCritereDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        BaremeCritereDTO result = baremeCritereService.save(baremeCritereDTO);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBaremeCritere(@PathVariable Long id) {
        baremeCritereService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
