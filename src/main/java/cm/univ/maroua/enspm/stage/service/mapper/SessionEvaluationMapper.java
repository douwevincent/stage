package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionEvaluationMapper extends EntityMapper<SessionEvaluationDTO, SessionEvaluation> {
}
