package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionEvaluationRepository extends JpaRepository<SessionEvaluation, Long> {
    Optional<SessionEvaluation> findFirstByCodeAcces(String codeAcces);

    Optional<SessionEvaluation> findByStageId(Long stageId);

    List<SessionEvaluation> findByStageEncadreurIdOrderByStageDateDebutAsc(Long encadreurId);
}
