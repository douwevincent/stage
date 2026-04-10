package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.service.dto.SessionEvaluationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct SessionEvaluationMapper.
 */
public interface SessionEvaluationMapper extends EntityMapper<SessionEvaluationDTO, SessionEvaluation> {
	SessionEvaluationDTO toDto(SessionEvaluation entity);

	@Mapping(target = "stage", ignore = true)
	SessionEvaluation toEntity(SessionEvaluationDTO dto);
}
