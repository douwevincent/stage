package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Localite;
import cm.univ.maroua.enspm.stage.service.dto.LocaliteDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EntrepriseMapper.class})
public interface LocaliteMapper extends EntityMapper<LocaliteDTO, Localite> {
}
