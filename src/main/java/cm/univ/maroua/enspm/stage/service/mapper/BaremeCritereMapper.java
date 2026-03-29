package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.BaremeCritere;
import cm.univ.maroua.enspm.stage.service.dto.BaremeCritereDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BaremeCritereMapper extends EntityMapper<BaremeCritereDTO, BaremeCritere> {
    @Mapping(source = "bareme.id", target = "baremeId")
    @Mapping(source = "bareme.code", target = "baremeCode")
    @Mapping(source = "critere.id", target = "critereId")
    @Mapping(source = "critere.libelle", target = "critereLibelle")
    BaremeCritereDTO toDto(BaremeCritere entity);

    @Mapping(source = "baremeId", target = "bareme.id")
    @Mapping(source = "critereId", target = "critere.id")
    BaremeCritere toEntity(BaremeCritereDTO dto);
}
