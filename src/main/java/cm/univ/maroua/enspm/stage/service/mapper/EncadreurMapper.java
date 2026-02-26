package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import cm.univ.maroua.enspm.stage.service.dto.EncadreurDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EncadreurMapper extends EntityMapper<EncadreurDTO, Encadreur> {
    @Mapping(source = "entreprise.id", target = "entrepriseId")
    EncadreurDTO toDto(Encadreur entity);

    @Mapping(source = "entrepriseId", target = "entreprise.id")
    Encadreur toEntity(EncadreurDTO dto);
}
