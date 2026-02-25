package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.service.dto.ParcoursDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SpecialiteMapper.class, NiveauMapper.class})
public interface ParcoursMapper extends EntityMapper<ParcoursDTO, Parcours> {
}
