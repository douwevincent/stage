package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Departement;
import cm.univ.maroua.enspm.stage.service.dto.DepartementDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartementMapper extends EntityMapper<DepartementDTO, Departement> {
}
