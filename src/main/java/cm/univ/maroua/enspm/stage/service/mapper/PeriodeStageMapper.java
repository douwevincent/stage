package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.service.dto.PeriodeStageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PeriodeStageMapper extends EntityMapper<PeriodeStageDTO, PeriodeStage> {
    @Mapping(source = "typeStage.id", target = "typeStageId")
    @Mapping(source = "anneeAcademique.id", target = "anneeAcademiqueId")
    PeriodeStageDTO toDto(PeriodeStage entity);

    @Mapping(source = "typeStageId", target = "typeStage.id")
    @Mapping(source = "anneeAcademiqueId", target = "anneeAcademique.id")
    PeriodeStage toEntity(PeriodeStageDTO dto);
}
