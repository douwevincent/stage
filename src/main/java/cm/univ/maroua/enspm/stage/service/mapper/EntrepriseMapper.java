package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Entreprise;
import cm.univ.maroua.enspm.stage.service.dto.EntrepriseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct EntrepriseMapper.
 */
public interface EntrepriseMapper extends EntityMapper<EntrepriseDTO, Entreprise> {
}
