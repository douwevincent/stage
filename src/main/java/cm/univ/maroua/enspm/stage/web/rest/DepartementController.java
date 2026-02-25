package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.DepartementService;
import cm.univ.maroua.enspm.stage.service.dto.DepartementDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/departements")
public class DepartementController {

    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @GetMapping
    public Page<DepartementDTO> getAllDepartements(Pageable pageable) {
        return departementService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartementDTO> getDepartement(@PathVariable Long id) {
        return departementService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DepartementDTO> createDepartement(@Valid @RequestBody DepartementDTO departementDTO)
            throws URISyntaxException {
        if (departementDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        DepartementDTO result = departementService.save(departementDTO);
        return ResponseEntity.created(new URI("/departements/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartementDTO> updateDepartement(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody DepartementDTO departementDTO) {
        if (departementDTO.id() == null || !id.equals(departementDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        DepartementDTO result = departementService.save(departementDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        departementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
