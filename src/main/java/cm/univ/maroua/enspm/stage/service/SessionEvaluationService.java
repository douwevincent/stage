package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;
import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.repository.SessionEvaluationRepository;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import cm.univ.maroua.enspm.stage.service.mapper.SessionEvaluationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
public class SessionEvaluationService {

    private final SessionEvaluationRepository sessionEvaluationRepository;
    private final SessionEvaluationMapper sessionEvaluationMapper;

    public SessionEvaluationService(SessionEvaluationRepository sessionEvaluationRepository,
            SessionEvaluationMapper sessionEvaluationMapper) {
        this.sessionEvaluationRepository = sessionEvaluationRepository;
        this.sessionEvaluationMapper = sessionEvaluationMapper;
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
}
