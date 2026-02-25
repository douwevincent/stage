package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.repository.SessionEvaluationRepository;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import cm.univ.maroua.enspm.stage.service.mapper.SessionEvaluationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
