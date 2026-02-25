package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.service.dto.TypeStageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeStageMapper extends EntityMapper<TypeStageDTO, TypeStage> {
}
