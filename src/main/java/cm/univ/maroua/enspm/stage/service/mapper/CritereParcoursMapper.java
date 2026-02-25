package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import cm.univ.maroua.enspm.stage.service.dto.CritereParcoursDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ParcoursMapper.class, CritereMapper.class})
public interface CritereParcoursMapper extends EntityMapper<CritereParcoursDTO, CritereParcours> {
}
