package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.service.dto.SpecialiteDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DepartementMapper.class})
public interface SpecialiteMapper extends EntityMapper<SpecialiteDTO, Specialite> {
}
