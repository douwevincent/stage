package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;
import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.domain.Note;
import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.repository.NoteRepository;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.repository.SessionEvaluationRepository;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationCategoryDetailDTO;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationCriterionDetailDTO;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationResultDetailDTO;
import cm.univ.maroua.enspm.stage.service.dto.EvaluationResultSummaryDTO;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import cm.univ.maroua.enspm.stage.service.mapper.SessionEvaluationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SessionEvaluationService {

    private final SessionEvaluationRepository sessionEvaluationRepository;
    private final SessionEvaluationMapper sessionEvaluationMapper;
    private final NoteRepository noteRepository;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final InscriptionRepository inscriptionRepository;
    private final EvaluationPdfService evaluationPdfService;

    public SessionEvaluationService(SessionEvaluationRepository sessionEvaluationRepository,
            SessionEvaluationMapper sessionEvaluationMapper,
            NoteRepository noteRepository,
            AnneeAcademiqueRepository anneeAcademiqueRepository,
            InscriptionRepository inscriptionRepository,
            EvaluationPdfService evaluationPdfService) {
        this.sessionEvaluationRepository = sessionEvaluationRepository;
        this.sessionEvaluationMapper = sessionEvaluationMapper;
        this.noteRepository = noteRepository;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.evaluationPdfService = evaluationPdfService;
    }

    public Page<SessionEvaluationDTO> findAll(Pageable pageable) {
        return sessionEvaluationRepository.findAll(pageable).map(sessionEvaluationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<SessionEvaluationDTO> findOne(Long id) {
        return sessionEvaluationRepository.findById(id).map(sessionEvaluationMapper::toDto);
    }

    public SessionEvaluationDTO save(SessionEvaluationDTO sessionEvaluationDTO) {
        SessionEvaluation sessionEvaluation = sessionEvaluationMapper.toEntity(sessionEvaluationDTO);
        sessionEvaluation = sessionEvaluationRepository.save(sessionEvaluation);
        return sessionEvaluationMapper.toDto(sessionEvaluation);
    }

    public void delete(Long id) {
        sessionEvaluationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
        public Page<EvaluationResultSummaryDTO> findEvaluatedWithTotals(
            Long niveauId,
            Long departementId,
            Long specialiteId,
            String q,
            Pageable pageable) {
        AnneeAcademique activeYear = anneeAcademiqueRepository.findByActifTrue()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                "Aucune annee academique active"));

        return sessionEvaluationRepository.searchEvaluatedByFilters(
                activeYear.getId(),
                niveauId,
                departementId,
                specialiteId,
                q,
                pageable)
                .map(this::toEvaluationSummary);
    }

        @Transactional(readOnly = true)
        public EvaluationResultDetailDTO getEvaluationDetail(Long sessionId) {
        SessionEvaluation session = sessionEvaluationRepository.findById(sessionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session d'evaluation introuvable"));

        Stage stage = session.getStage();
        if (stage == null || stage.getEtudiant() == null || stage.getEtudiant().getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                "Les informations de l'etudiant sont introuvables pour cette session");
        }

        AnneeAcademique activeYear = anneeAcademiqueRepository.findByActifTrue()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                "Aucune annee academique active"));

        Inscription inscription = inscriptionRepository
            .findFirstByEtudiantIdAndAnneeAcademiqueIdOrderByIdDesc(stage.getEtudiant().getId(), activeYear.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                "Aucune inscription active trouvee pour l'etudiant"));

        List<Note> notes = noteRepository.findBySessionId(session.getId());
        ScoreSummary scoreSummary = computeScoreSummaryFromNotes(notes);

        Map<String, List<EvaluationCriterionDetailDTO>> grouped = new LinkedHashMap<>();
        notes.stream()
            .sorted(Comparator.comparing((Note n) -> normalizeCategory(n.getCritere() != null ? n.getCritere().getCategorie() : null))
                .thenComparing(n -> n.getCritere() != null && n.getCritere().getLibelle() != null
                    ? n.getCritere().getLibelle()
                    : ""))
            .forEach(note -> {
                String category = normalizeCategory(note.getCritere() != null ? note.getCritere().getCategorie() : null);
                grouped.computeIfAbsent(category, key -> new java.util.ArrayList<>())
                    .add(new EvaluationCriterionDetailDTO(
                        note.getCritere() != null ? note.getCritere().getId() : null,
                        note.getCritere() != null ? note.getCritere().getLibelle() : null,
                        note.getBaremeCritere() != null ? note.getBaremeCritere().getCoefficient() : null,
                        note.getValeur(),
                        note.getCommentaire()));
            });

        List<EvaluationCategoryDetailDTO> categories = grouped.entrySet().stream()
            .map(entry -> new EvaluationCategoryDetailDTO(entry.getKey(), entry.getValue()))
            .toList();

        String departement = inscription.getParcours() != null
            && inscription.getParcours().getSpecialite() != null
            && inscription.getParcours().getSpecialite().getDepartement() != null
            ? inscription.getParcours().getSpecialite().getDepartement().getIntitule()
            : null;
        String niveau = inscription.getParcours() != null && inscription.getParcours().getNiveau() != null
            ? inscription.getParcours().getNiveau().getLibelle()
            : null;
        String specialite = inscription.getParcours() != null && inscription.getParcours().getSpecialite() != null
            ? inscription.getParcours().getSpecialite().getIntitule()
            : null;

        return new EvaluationResultDetailDTO(
            stage.getId(),
            session.getId(),
            session.getStatut(),
            stage.getEtudiant().getNom(),
            stage.getEtudiant().getMatricule(),
            stage.getEtudiant().getEmail(),
            stage.getEtudiant().getTelephone(),
            activeYear.getLibelle(),
            departement,
            niveau,
            specialite,
            stage.getEntreprise() != null ? stage.getEntreprise().getNom() : null,
            stage.getEncadreur() != null ? stage.getEncadreur().getNom() : null,
            stage.getDateDebut(),
            stage.getDateFin(),
            scoreSummary.totalScore(),
            scoreSummary.maxScore(),
            categories);
        }

        @Transactional(readOnly = true)
        public byte[] generateEditableEvaluationPdf(Long sessionId) {
        EvaluationResultDetailDTO detail = getEvaluationDetail(sessionId);
        return evaluationPdfService.buildEditableEvaluationSheet(detail);
        }

    /**
     * Crée ou réutilise une {@link SessionEvaluation} pour le stage donné en s'assurant
     * qu'un code d'accès court (12 caractères Base64URL) est présent.
     */
    public SessionEvaluation ensureSessionWithCode(Stage stage, LocalDate dateLimite) {
        Optional<SessionEvaluation> existing = sessionEvaluationRepository.findByStageId(stage.getId());
        if (existing.isPresent()) {
            SessionEvaluation session = existing.get();
            boolean dirty = false;
            if (session.getCodeAcces() == null || session.getCodeAcces().isBlank()) {
                session.setCodeAcces(generateShortCode());
                dirty = true;
            }
            if (session.getDateLimite() == null && dateLimite != null) {
                session.setDateLimite(dateLimite);
                dirty = true;
            }
            return dirty ? sessionEvaluationRepository.save(session) : session;
        }
        SessionEvaluation session = new SessionEvaluation();
        session.setStage(stage);
        session.setCodeAcces(generateShortCode());
        session.setStatut(SessionEvaluationStatut.EN_ATTENTE);
        session.setDateLimite(dateLimite);
        return sessionEvaluationRepository.save(session);
    }

    /**
     * Génère un code court de 12 caractères URL-safe (Base64URL sans padding, 9 octets aléatoires).
     * Entropie : 72 bits — cryptographiquement sûr via {@link SecureRandom}.
     */
    private static String generateShortCode() {
        byte[] bytes = new byte[9];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private EvaluationResultSummaryDTO toEvaluationSummary(SessionEvaluation session) {
        Stage stage = session.getStage();
        ScoreSummary scoreSummary = computeScoreSummary(session.getId());
        Inscription inscription = resolveCurrentInscription(stage);

        String departement = inscription != null
            && inscription.getParcours() != null
            && inscription.getParcours().getSpecialite() != null
            && inscription.getParcours().getSpecialite().getDepartement() != null
            ? inscription.getParcours().getSpecialite().getDepartement().getIntitule()
            : null;
        String niveau = inscription != null
            && inscription.getParcours() != null
            && inscription.getParcours().getNiveau() != null
            ? inscription.getParcours().getNiveau().getLibelle()
            : null;
        String specialite = inscription != null
            && inscription.getParcours() != null
            && inscription.getParcours().getSpecialite() != null
            ? inscription.getParcours().getSpecialite().getIntitule()
            : null;

        return new EvaluationResultSummaryDTO(
                stage != null ? stage.getId() : null,
                session.getId(),
                session.getStatut(),
                stage != null && stage.getEtudiant() != null ? stage.getEtudiant().getNom() : null,
                stage != null && stage.getEtudiant() != null ? stage.getEtudiant().getMatricule() : null,
            departement,
            niveau,
            specialite,
                stage != null && stage.getEntreprise() != null ? stage.getEntreprise().getNom() : null,
                stage != null ? stage.getDateDebut() : null,
                stage != null ? stage.getDateFin() : null,
                scoreSummary.totalScore(),
                scoreSummary.maxScore());
    }

    private ScoreSummary computeScoreSummary(Long sessionId) {
        if (sessionId == null) {
            return new ScoreSummary(0f, 0f);
        }

        List<Note> notes = noteRepository.findBySessionId(sessionId);
        return computeScoreSummaryFromNotes(notes);
    }

    private ScoreSummary computeScoreSummaryFromNotes(List<Note> notes) {
        if (notes.isEmpty()) {
            return new ScoreSummary(0f, 0f);
        }

        float totalScore = 0f;
        float maxScore = 0f;
        for (Note note : notes) {
            if (note.getValeur() != null) {
                totalScore += note.getValeur();
            }
            if (note.getBaremeCritere() != null && note.getBaremeCritere().getCoefficient() != null) {
                maxScore += note.getBaremeCritere().getCoefficient();
            }
        }
        return new ScoreSummary(totalScore, maxScore);
    }

    private Inscription resolveCurrentInscription(Stage stage) {
        if (stage == null || stage.getEtudiant() == null || stage.getEtudiant().getId() == null) {
            return null;
        }

        Optional<AnneeAcademique> activeYear = anneeAcademiqueRepository.findByActifTrue();
        if (activeYear.isEmpty()) {
            return null;
        }

        return inscriptionRepository
                .findFirstByEtudiantIdAndAnneeAcademiqueIdOrderByIdDesc(stage.getEtudiant().getId(), activeYear.get().getId())
                .orElse(null);
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "Autres";
        }
        return category.trim();
    }

    private record ScoreSummary(float totalScore, float maxScore) {
    }
}
