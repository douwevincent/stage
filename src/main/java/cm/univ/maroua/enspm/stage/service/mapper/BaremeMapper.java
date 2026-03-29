package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Bareme;
import cm.univ.maroua.enspm.stage.service.dto.BaremeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BaremeMapper extends EntityMapper<BaremeDTO, Bareme> {
}
