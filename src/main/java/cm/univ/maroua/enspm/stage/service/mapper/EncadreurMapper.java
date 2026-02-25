package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import cm.univ.maroua.enspm.stage.service.dto.EncadreurDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EntrepriseMapper.class})
public interface EncadreurMapper extends EntityMapper<EncadreurDTO, Encadreur> {
}
