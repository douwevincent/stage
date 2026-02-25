package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.EncadreurService;
import cm.univ.maroua.enspm.stage.service.dto.EncadreurDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/encadreurs")
public class EncadreurController {

    private final EncadreurService encadreurService;

    public EncadreurController(EncadreurService encadreurService) {
        this.encadreurService = encadreurService;
    }

    @GetMapping
    public Page<EncadreurDTO> getAllEncadreurs(Pageable pageable) {
        return encadreurService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncadreurDTO> getEncadreur(@PathVariable Long id) {
        return encadreurService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EncadreurDTO> createEncadreur(@Valid @RequestBody EncadreurDTO encadreurDTO)
            throws URISyntaxException {
        if (encadreurDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        EncadreurDTO result = encadreurService.save(encadreurDTO);
        return ResponseEntity.created(new URI("/encadreurs/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EncadreurDTO> updateEncadreur(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody EncadreurDTO encadreurDTO) {
        if (encadreurDTO.id() == null || !id.equals(encadreurDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        EncadreurDTO result = encadreurService.save(encadreurDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncadreur(@PathVariable Long id) {
        encadreurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
