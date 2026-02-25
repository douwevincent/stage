package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.service.dto.AnneeAcademiqueDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnneeAcademiqueMapper extends EntityMapper<AnneeAcademiqueDTO, AnneeAcademique> {
}
