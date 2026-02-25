package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.service.dto.NiveauDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NiveauMapper extends EntityMapper<NiveauDTO, Niveau> {
}
