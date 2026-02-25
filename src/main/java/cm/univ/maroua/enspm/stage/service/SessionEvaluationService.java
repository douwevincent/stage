package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.repository.SessionEvaluationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SessionEvaluationService {

    private final SessionEvaluationRepository sessionEvaluationRepository;

    public SessionEvaluationService(SessionEvaluationRepository sessionEvaluationRepository) {
        this.sessionEvaluationRepository = sessionEvaluationRepository;
    }

    public Page<SessionEvaluation> findAll(Pageable pageable) {
        return sessionEvaluationRepository.findAll(pageable);
    }

    public Optional<SessionEvaluation> findOne(Long id) {
        return sessionEvaluationRepository.findById(id);
    }

    public SessionEvaluation save(SessionEvaluation sessionEvaluation) {
        return sessionEvaluationRepository.save(sessionEvaluation);
    }

    public void delete(Long id) {
        sessionEvaluationRepository.deleteById(id);
    }
}
