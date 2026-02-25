package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.service.dto.StageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EtudiantMapper.class, LocaliteMapper.class, EncadreurMapper.class, TypeStageMapper.class, AnneeAcademiqueMapper.class, SessionEvaluationMapper.class})
public interface StageMapper extends EntityMapper<StageDTO, Stage> {
}
