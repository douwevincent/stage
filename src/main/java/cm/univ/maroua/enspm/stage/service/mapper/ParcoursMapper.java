package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.service.dto.ParcoursDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParcoursMapper extends EntityMapper<ParcoursDTO, Parcours> {
    @Mapping(source = "specialite.id", target = "specialiteId")
    @Mapping(source = "niveau.id", target = "niveauId")
    ParcoursDTO toDto(Parcours entity);

    @Mapping(source = "specialiteId", target = "specialite.id")
    @Mapping(source = "niveauId", target = "niveau.id")
    Parcours toEntity(ParcoursDTO dto);
}
