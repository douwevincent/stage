package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.NiveauService;
import cm.univ.maroua.enspm.stage.service.dto.NiveauDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/niveaus")
public class NiveauController {

    private final NiveauService niveauService;

    public NiveauController(NiveauService niveauService) {
        this.niveauService = niveauService;
    }

    @GetMapping
    public Page<NiveauDTO> getAllNiveaus(Pageable pageable) {
        return niveauService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NiveauDTO> getNiveau(@PathVariable Long id) {
        return niveauService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NiveauDTO> createNiveau(@Valid @RequestBody NiveauDTO niveauDTO) throws URISyntaxException {
        if (niveauDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        NiveauDTO result = niveauService.save(niveauDTO);
        return ResponseEntity.created(new URI("/niveaus/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NiveauDTO> updateNiveau(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody NiveauDTO niveauDTO) {
        if (niveauDTO.id() == null || !id.equals(niveauDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        NiveauDTO result = niveauService.save(niveauDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNiveau(@PathVariable Long id) {
        niveauService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
