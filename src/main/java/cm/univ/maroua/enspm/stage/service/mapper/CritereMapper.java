package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Critere;
import cm.univ.maroua.enspm.stage.service.dto.CritereDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CritereMapper extends EntityMapper<CritereDTO, Critere> {
}
