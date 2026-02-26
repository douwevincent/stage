package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.service.dto.SpecialiteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecialiteMapper extends EntityMapper<SpecialiteDTO, Specialite> {
    @Mapping(source = "departement.id", target = "departementId")
    SpecialiteDTO toDto(Specialite entity);

    @Mapping(source = "departementId", target = "departement.id")
    Specialite toEntity(SpecialiteDTO dto);
}
