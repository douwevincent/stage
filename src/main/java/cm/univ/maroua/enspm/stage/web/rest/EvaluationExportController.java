package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.repository.NiveauRepository;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import cm.univ.maroua.enspm.stage.repository.TypeStageRepository;
import cm.univ.maroua.enspm.stage.service.EvaluationExportService;
import cm.univ.maroua.enspm.stage.service.SessionEvaluationService;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationExportDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/evaluation-exports")
public class EvaluationExportController {

    private final SessionEvaluationService sessionEvaluationService;
    private final EvaluationExportService evaluationExportService;
    private final NiveauRepository niveauRepository;
    private final ParcoursRepository parcoursRepository;
    private final TypeStageRepository typeStageRepository;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    public EvaluationExportController(SessionEvaluationService sessionEvaluationService,
                                      EvaluationExportService evaluationExportService,
                                      NiveauRepository niveauRepository,
                                      ParcoursRepository parcoursRepository,
                                      TypeStageRepository typeStageRepository,
                                      AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.sessionEvaluationService = sessionEvaluationService;
        this.evaluationExportService = evaluationExportService;
        this.niveauRepository = niveauRepository;
        this.parcoursRepository = parcoursRepository;
        this.typeStageRepository = typeStageRepository;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    // ========== Export by Niveau ==========

    @GetMapping("/by-niveau/{niveauId}/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportPdfByNiveau(@PathVariable Long niveauId,
                                                     @RequestParam(required = false) Long anneeAcademiqueId) {
        Niveau niveau = niveauRepository.findById(niveauId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niveau non trouvé"));

        String anneeAcademique = getAnneeAcademique(anneeAcademiqueId);
        Map<Long, List<SessionEvaluationExportDTO>> data = sessionEvaluationService.getEvaluatedByNiveauIdGroupedByParcours(niveauId);

        byte[] pdfContent = evaluationExportService.generatePdfByNiveau(niveauId, niveau.getLibelle(), anneeAcademique, data);

        String fileName = "Resultats_Niveau_" + niveauId + "_" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfContent);
    }

    @GetMapping("/by-niveau/{niveauId}/excel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportExcelByNiveau(@PathVariable Long niveauId,
                                                       @RequestParam(required = false) Long anneeAcademiqueId) {
        Niveau niveau = niveauRepository.findById(niveauId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niveau non trouvé"));

        String anneeAcademique = getAnneeAcademique(anneeAcademiqueId);
        Map<Long, List<SessionEvaluationExportDTO>> data = sessionEvaluationService.getEvaluatedByNiveauIdGroupedByParcours(niveauId);

        byte[] excelContent = evaluationExportService.generateExcelByNiveau(niveauId, niveau.getLibelle(), anneeAcademique, data);

        String fileName = "Resultats_Niveau_" + niveauId + "_" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelContent);
    }

    // ========== Export by Parcours ==========

    @GetMapping("/by-parcours/{parcoursId}/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportPdfByParcours(@PathVariable Long parcoursId,
                                                       @RequestParam(required = false) Long anneeAcademiqueId) {
        Parcours parcours = parcoursRepository.findById(parcoursId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcours non trouvé"));

        String anneeAcademique = getAnneeAcademique(anneeAcademiqueId);
        String parcoursLabel = parcours.getSpecialite().getIntitule() + " - " + parcours.getNiveau().getLibelle();
        List<SessionEvaluationExportDTO> data = sessionEvaluationService.getEvaluatedByParcoursId(parcoursId);

        byte[] pdfContent = evaluationExportService.generatePdfByParcours(parcoursId, parcoursLabel, anneeAcademique, data);

        String fileName = "Resultats_Parcours_" + parcoursId + "_" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfContent);
    }

    @GetMapping("/by-parcours/{parcoursId}/excel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportExcelByParcours(@PathVariable Long parcoursId,
                                                         @RequestParam(required = false) Long anneeAcademiqueId) {
        Parcours parcours = parcoursRepository.findById(parcoursId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcours non trouvé"));

        String anneeAcademique = getAnneeAcademique(anneeAcademiqueId);
        String parcoursLabel = parcours.getSpecialite().getIntitule() + " - " + parcours.getNiveau().getLibelle();
        List<SessionEvaluationExportDTO> data = sessionEvaluationService.getEvaluatedByParcoursId(parcoursId);

        byte[] excelContent = evaluationExportService.generateExcelByParcours(parcoursId, parcoursLabel, anneeAcademique, data);

        String fileName = "Resultats_Parcours_" + parcoursId + "_" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelContent);
    }

    // ========== Export by Type Stage ==========

    @GetMapping("/by-type-stage/{typeStageId}/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportPdfByTypeStage(@PathVariable Long typeStageId,
                                                        @RequestParam(required = false) Long anneeAcademiqueId) {
        TypeStage typeStage = typeStageRepository.findById(typeStageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de stage non trouvé"));

        String anneeAcademique = getAnneeAcademique(anneeAcademiqueId);
        Map<Long, List<SessionEvaluationExportDTO>> data = sessionEvaluationService.getEvaluatedByTypeStageIdGroupedByParcours(typeStageId);

        byte[] pdfContent = evaluationExportService.generatePdfByTypeStage(typeStageId, typeStage.getLibelle(), anneeAcademique, data);

        String fileName = "Resultats_TypeStage_" + typeStageId + "_" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfContent);
    }

    @GetMapping("/by-type-stage/{typeStageId}/excel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> exportExcelByTypeStage(@PathVariable Long typeStageId,
                                                          @RequestParam(required = false) Long anneeAcademiqueId) {
        TypeStage typeStage = typeStageRepository.findById(typeStageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de stage non trouvé"));

        String anneeAcademique = getAnneeAcademique(anneeAcademiqueId);
        Map<Long, List<SessionEvaluationExportDTO>> data = sessionEvaluationService.getEvaluatedByTypeStageIdGroupedByParcours(typeStageId);

        byte[] excelContent = evaluationExportService.generateExcelByTypeStage(typeStageId, typeStage.getLibelle(), anneeAcademique, data);

        String fileName = "Resultats_TypeStage_" + typeStageId + "_" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelContent);
    }

    // ========== Helper Methods ==========

    private String getAnneeAcademique(Long anneeAcademiqueId) {
        if (anneeAcademiqueId != null) {
            Optional<AnneeAcademique> annee = anneeAcademiqueRepository.findById(anneeAcademiqueId);
            return annee.map(AnneeAcademique::getLibelle).orElseGet(() -> getActiveAnneeAcademique());
        }
        return getActiveAnneeAcademique();
    }

    private String getActiveAnneeAcademique() {
        return anneeAcademiqueRepository.findByActifTrue()
            .map(AnneeAcademique::getLibelle)
            .orElse("N/A");
    }
}
