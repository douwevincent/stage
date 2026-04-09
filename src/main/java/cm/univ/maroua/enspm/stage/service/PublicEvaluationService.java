package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.domain.Bareme;
import cm.univ.maroua.enspm.stage.domain.BaremeCritere;
import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.domain.Note;
import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;
import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.repository.BaremeCritereRepository;
import cm.univ.maroua.enspm.stage.repository.BaremeRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.repository.NoteRepository;
import cm.univ.maroua.enspm.stage.repository.SessionEvaluationRepository;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationCategoryDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationCriterionDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationFormDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationNoteInputDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationStageItemDTO;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationSubmitRequest;
import cm.univ.maroua.enspm.stage.service.dto.publicevaluation.PublicEvaluationSubmitResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
/**
 * Service metier PublicEvaluationService.
 */
public class PublicEvaluationService {

    private final SessionEvaluationRepository sessionEvaluationRepository;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final InscriptionRepository inscriptionRepository;
    private final BaremeRepository baremeRepository;
    private final BaremeCritereRepository baremeCritereRepository;
    private final NoteRepository noteRepository;

    public PublicEvaluationService(
            SessionEvaluationRepository sessionEvaluationRepository,
            AnneeAcademiqueRepository anneeAcademiqueRepository,
            InscriptionRepository inscriptionRepository,
            BaremeRepository baremeRepository,
            BaremeCritereRepository baremeCritereRepository,
            NoteRepository noteRepository) {
        this.sessionEvaluationRepository = sessionEvaluationRepository;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.baremeRepository = baremeRepository;
        this.baremeCritereRepository = baremeCritereRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional(readOnly = true)
    public List<PublicEvaluationStageItemDTO> getStagesByAccessCode(String codeAcces) {
        SessionEvaluation anchorSession = resolveAnchorSession(codeAcces);
        Long encadreurId = requireEncadreurId(anchorSession);
        LocalDate today = LocalDate.now();

        return sessionEvaluationRepository.findByStageEncadreurIdOrderByStageDateDebutAsc(encadreurId)
                .stream()
                .filter(session -> session.getStatut() != SessionEvaluationStatut.TERMINEE)
                .filter(session -> session.getDateLimite() == null || !session.getDateLimite().isBefore(today))
                .map(this::toStageItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicEvaluationFormDTO getEvaluationForm(String codeAcces, Long stageId) {
        SessionEvaluation session = resolveSessionForStage(codeAcces, stageId);
        validateSessionAvailability(session);

        Stage stage = requireStage(session);
        Bareme bareme = resolveBaremeForStage(stage);
        List<BaremeCritere> baremeCriteres = baremeCritereRepository.findByBaremeId(bareme.getId());

        if (baremeCriteres.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Aucun critere n'est associe au bareme selectionne");
        }

        List<PublicEvaluationCategoryDTO> categories = groupByCategory(baremeCriteres);

        return new PublicEvaluationFormDTO(
                stage.getId(),
                session.getId(),
                stage.getEtudiant() != null ? stage.getEtudiant().getNom() : null,
                stage.getEtudiant() != null ? stage.getEtudiant().getMatricule() : null,
                stage.getEntreprise() != null ? stage.getEntreprise().getNom() : null,
                stage.getDateDebut(),
                stage.getDateFin(),
                categories);
    }

    public PublicEvaluationSubmitResponse submit(String codeAcces, PublicEvaluationSubmitRequest request) {
        SessionEvaluation session = resolveSessionForStage(codeAcces, request.stageId());
        validateSessionAvailability(session);

        Stage stage = requireStage(session);
        Bareme bareme = resolveBaremeForStage(stage);
        List<BaremeCritere> baremeCriteres = baremeCritereRepository.findByBaremeId(bareme.getId());

        if (baremeCriteres.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Aucun critere n'est associe au bareme selectionne");
        }

        Map<Long, BaremeCritere> baremeCritereByCritere = new LinkedHashMap<>();
        for (BaremeCritere baremeCritere : baremeCriteres) {
            if (baremeCritere.getCritere() != null && baremeCritere.getCritere().getId() != null) {
                baremeCritereByCritere.put(baremeCritere.getCritere().getId(), baremeCritere);
            }
        }

        if (request.notes().size() != baremeCritereByCritere.size()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Toutes les notes des criteres du bareme sont obligatoires");
        }

        Map<Long, PublicEvaluationNoteInputDTO> submitted = new LinkedHashMap<>();
        for (PublicEvaluationNoteInputDTO noteInput : request.notes()) {
            if (submitted.put(noteInput.critereId(), noteInput) != null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "Un critere ne peut etre note qu'une seule fois");
            }
        }

        if (!submitted.keySet().equals(baremeCritereByCritere.keySet())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Les criteres soumis ne correspondent pas au bareme de ce stage");
        }

        List<Note> notes = new ArrayList<>();
        LocalDate dateAttribution = LocalDate.now();

        for (Map.Entry<Long, PublicEvaluationNoteInputDTO> entry : submitted.entrySet()) {
            Long critereId = entry.getKey();
            PublicEvaluationNoteInputDTO input = entry.getValue();
            BaremeCritere baremeCritere = baremeCritereByCritere.get(critereId);

            if (input.valeur() < 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "La note ne peut pas etre negative");
            }

            float max = baremeCritere.getCoefficient() == null ? 0f : baremeCritere.getCoefficient();
            if (input.valeur() > max) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "La note du critere " + baremeCritere.getCritere().getLibelle() + " depasse le maximum autorise");
            }

            Note note = new Note();
            note.setSession(session);
            note.setCritere(baremeCritere.getCritere());
            note.setBaremeCritere(baremeCritere);
            note.setValeur(input.valeur());
            note.setCommentaire(input.commentaire());
            note.setDateAttribution(dateAttribution);
            notes.add(note);
        }

        noteRepository.deleteBySessionId(session.getId());
        noteRepository.saveAll(notes);

        session.setStatut(SessionEvaluationStatut.TERMINEE);
        sessionEvaluationRepository.save(session);

        return new PublicEvaluationSubmitResponse(
                session.getId(),
                session.getStatut(),
                "Evaluation enregistree avec succes");
    }

    private SessionEvaluation resolveAnchorSession(String codeAcces) {
        return sessionEvaluationRepository.findFirstByCodeAcces(codeAcces)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lien d'evaluation invalide"));
    }

    private SessionEvaluation resolveSessionForStage(String codeAcces, Long stageId) {
        SessionEvaluation anchorSession = resolveAnchorSession(codeAcces);
        Long encadreurId = requireEncadreurId(anchorSession);

        return sessionEvaluationRepository.findByStageEncadreurIdOrderByStageDateDebutAsc(encadreurId)
                .stream()
                .filter(session -> {
                    Stage stage = session.getStage();
                    return stage != null && Objects.equals(stage.getId(), stageId);
                })
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Stage introuvable pour ce lien d'evaluation"));
    }

    private Long requireEncadreurId(SessionEvaluation session) {
        Stage stage = requireStage(session);
        if (stage.getEncadreur() == null || stage.getEncadreur().getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Aucun encadreur n'est associe a ce stage");
        }
        return stage.getEncadreur().getId();
    }

    private Stage requireStage(SessionEvaluation session) {
        Stage stage = session.getStage();
        if (stage == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Aucun stage n'est associe a cette session");
        }
        return stage;
    }

    private void validateSessionAvailability(SessionEvaluation session) {
        if (session.getStatut() == SessionEvaluationStatut.TERMINEE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cette evaluation est deja terminee");
        }
        LocalDate today = LocalDate.now();
        if (session.getDateLimite() != null && session.getDateLimite().isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.GONE,
                    "Le lien d'evaluation est expire");
        }
    }

    private Bareme resolveBaremeForStage(Stage stage) {
        AnneeAcademique activeYear = anneeAcademiqueRepository.findByActifTrue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "Aucune annee academique active"));

        if (stage.getEtudiant() == null || stage.getEtudiant().getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Le stage n'est pas associe a un etudiant");
        }

        Inscription inscription = inscriptionRepository
                .findFirstByEtudiantIdAndAnneeAcademiqueIdOrderByIdDesc(stage.getEtudiant().getId(), activeYear.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "Aucune inscription active trouvee pour l'etudiant"));

        Bareme bareme = null;
        if (inscription.getParcours() != null) {
            bareme = inscription.getParcours().getBareme();
        }

        if (bareme == null) {
            bareme = baremeRepository.findByParDefautTrueAndActifTrue()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                            "Aucun bareme associe au parcours et aucun bareme par defaut actif"));
        }

        return bareme;
    }

    private PublicEvaluationStageItemDTO toStageItem(SessionEvaluation session) {
        Stage stage = requireStage(session);
        return new PublicEvaluationStageItemDTO(
                stage.getId(),
                session.getId(),
                stage.getEtudiant() != null ? stage.getEtudiant().getNom() : null,
                stage.getEtudiant() != null ? stage.getEtudiant().getMatricule() : null,
                stage.getEntreprise() != null ? stage.getEntreprise().getNom() : null,
                stage.getDateDebut(),
                stage.getDateFin(),
                session.getDateLimite());
    }

    private List<PublicEvaluationCategoryDTO> groupByCategory(List<BaremeCritere> baremeCriteres) {
        Map<String, List<PublicEvaluationCriterionDTO>> grouped = new LinkedHashMap<>();

        baremeCriteres.stream()
                .filter(baremeCritere -> baremeCritere.getCritere() != null)
                .sorted(Comparator.comparing(bc -> normalizeCategory(bc.getCritere().getCategorie())))
                .forEach(baremeCritere -> {
                    String category = normalizeCategory(baremeCritere.getCritere().getCategorie());
                    grouped.computeIfAbsent(category, key -> new ArrayList<>())
                            .add(new PublicEvaluationCriterionDTO(
                                    baremeCritere.getCritere().getId(),
                                    baremeCritere.getCritere().getLibelle(),
                                    category,
                                    baremeCritere.getCoefficient()));
                });

        return grouped.entrySet().stream()
                .map(entry -> new PublicEvaluationCategoryDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "Autres";
        }
        return category.trim();
    }
}
