package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.Statut;
import cm.univ.maroua.enspm.stage.service.StageService;
import cm.univ.maroua.enspm.stage.service.dto.StageDTO;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public Page<StageDTO> getAllStages(
            @RequestParam(required = false) String statut,
            Pageable pageable) {
        if (statut != null && !statut.isBlank()) {
            return stageService.findByStatut(Statut.valueOf(statut), pageable);
        }
        return stageService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageDTO> getStage(@PathVariable Long id) {
        return stageService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StageDTO> createStage(@Valid @RequestBody StageDTO stageDTO) throws URISyntaxException {
        if (stageDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        StageDTO result = stageService.save(stageDTO);
        return ResponseEntity.created(new URI("/stages/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageDTO> updateStage(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody StageDTO stageDTO) {
        if (stageDTO.id() == null || !id.equals(stageDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        StageDTO result = stageService.update(id, stageDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @PostMapping(value = "/declarer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StageDTO> declarerStage(
            @RequestParam String etudiantMatricule,
            @RequestParam(required = false) Long entrepriseId,
            @RequestParam(required = false) String entrepriseNom,
            @RequestParam(required = false) String entrepriseSecteur,
            @RequestParam String ville,
            @RequestParam String adresse,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestPart("autorisation") MultipartFile autorisation) throws IOException, URISyntaxException {
        StageDTO result = stageService.declarer(
                etudiantMatricule, entrepriseId, entrepriseNom, entrepriseSecteur,
                ville, adresse, dateDebut, dateFin, autorisation);
        return ResponseEntity.created(new URI("/stages/" + result.id())).body(result);
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<StageDTO> validerStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.valider(id));
    }

    @PatchMapping("/{id}/rejeter")
    public ResponseEntity<StageDTO> rejeterStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.rejeter(id));
    }

    @PatchMapping("/{id}/assigner-etudiant")
    public ResponseEntity<StageDTO> assignerEtudiant(
            @PathVariable Long id,
            @RequestParam Long etudiantId) {
        return ResponseEntity.ok(stageService.assignerEtudiant(id, etudiantId));
    }

    @PatchMapping("/{id}/assigner-encadreur")
    public ResponseEntity<StageDTO> assignerEncadreur(
            @PathVariable Long id,
            @RequestParam Long encadreurId) {
        return ResponseEntity.ok(stageService.assignerEncadreur(id, encadreurId));
    }

    @GetMapping("/{id}/autorisation")
    public ResponseEntity<Resource> getAutorisation(@PathVariable Long id) throws IOException {
        Resource resource = stageService.loadAutorisation(id);
        String contentType;
        try {
            contentType = Files.probeContentType(Paths.get(resource.getURI()));
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long id) {
        stageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

