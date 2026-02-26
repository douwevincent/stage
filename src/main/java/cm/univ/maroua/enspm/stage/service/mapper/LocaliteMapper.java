package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Localite;
import cm.univ.maroua.enspm.stage.service.dto.LocaliteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocaliteMapper extends EntityMapper<LocaliteDTO, Localite> {
    @Mapping(source = "entreprise.id", target = "entrepriseId")
    LocaliteDTO toDto(Localite entity);

    @Mapping(source = "entrepriseId", target = "entreprise.id")
    Localite toEntity(LocaliteDTO dto);
}
