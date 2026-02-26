package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import cm.univ.maroua.enspm.stage.service.dto.CritereParcoursDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CritereParcoursMapper extends EntityMapper<CritereParcoursDTO, CritereParcours> {
    @Mapping(source = "parcours.id", target = "parcoursId")
    @Mapping(source = "critere.id", target = "critereId")
    CritereParcoursDTO toDto(CritereParcours entity);

    @Mapping(source = "parcoursId", target = "parcours.id")
    @Mapping(source = "critereId", target = "critere.id")
    CritereParcours toEntity(CritereParcoursDTO dto);
}
