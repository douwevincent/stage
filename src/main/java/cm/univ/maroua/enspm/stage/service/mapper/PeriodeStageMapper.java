package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.service.dto.PeriodeStageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TypeStageMapper.class, AnneeAcademiqueMapper.class})
public interface PeriodeStageMapper extends EntityMapper<PeriodeStageDTO, PeriodeStage> {
}
