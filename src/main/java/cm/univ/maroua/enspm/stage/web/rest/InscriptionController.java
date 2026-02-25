package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.InscriptionService;
import cm.univ.maroua.enspm.stage.service.dto.InscriptionDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @GetMapping
    public Page<InscriptionDTO> getAllInscriptions(Pageable pageable) {
        return inscriptionService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscriptionDTO> getInscription(@PathVariable Long id) {
        return inscriptionService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InscriptionDTO> createInscription(@Valid @RequestBody InscriptionDTO inscriptionDTO)
            throws URISyntaxException {
        if (inscriptionDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        InscriptionDTO result = inscriptionService.save(inscriptionDTO);
        return ResponseEntity.created(new URI("/inscriptions/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscriptionDTO> updateInscription(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody InscriptionDTO inscriptionDTO) {
        if (inscriptionDTO.id() == null || !id.equals(inscriptionDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        InscriptionDTO result = inscriptionService.save(inscriptionDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscription(@PathVariable Long id) {
        inscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
