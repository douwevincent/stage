package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.SpecialiteService;
import cm.univ.maroua.enspm.stage.service.dto.SpecialiteDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/specialites")
public class SpecialiteController {

    private final SpecialiteService specialiteService;

    public SpecialiteController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public Page<SpecialiteDTO> getAllSpecialites(Pageable pageable) {
        return specialiteService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialiteDTO> getSpecialite(@PathVariable Long id) {
        return specialiteService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SpecialiteDTO> createSpecialite(@Valid @RequestBody SpecialiteDTO specialiteDTO)
            throws URISyntaxException {
        if (specialiteDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        SpecialiteDTO result = specialiteService.save(specialiteDTO);
        return ResponseEntity.created(new URI("/specialites/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialiteDTO> updateSpecialite(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody SpecialiteDTO specialiteDTO) {
        if (specialiteDTO.id() == null || !id.equals(specialiteDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        SpecialiteDTO result = specialiteService.save(specialiteDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialite(@PathVariable Long id) {
        specialiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
