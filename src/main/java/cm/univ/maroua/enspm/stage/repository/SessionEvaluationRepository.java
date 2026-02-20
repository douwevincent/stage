package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionEvaluationRepository extends JpaRepository<SessionEvaluation, Long> {
}
