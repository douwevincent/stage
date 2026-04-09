package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.BaremeService;
import cm.univ.maroua.enspm.stage.service.dto.BaremeDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/baremes")
/**
 * Controleur REST BaremeController.
 */
public class BaremeController {

    private final BaremeService baremeService;

    public BaremeController(BaremeService baremeService) {
        this.baremeService = baremeService;
    }

    @GetMapping
    public Page<BaremeDTO> getAllBaremes(Pageable pageable) {
        return baremeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaremeDTO> getBareme(@PathVariable Long id) {
        return baremeService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BaremeDTO> createBareme(@Valid @RequestBody BaremeDTO baremeDTO) throws URISyntaxException {
        if (baremeDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        BaremeDTO result = baremeService.save(baremeDTO);
        return ResponseEntity.created(new URI("/baremes/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaremeDTO> updateBareme(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody BaremeDTO baremeDTO) {
        if (baremeDTO.id() == null || !id.equals(baremeDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        BaremeDTO result = baremeService.save(baremeDTO);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBareme(@PathVariable Long id) {
        baremeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
