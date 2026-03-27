package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.service.dto.NiveauDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NiveauMapper extends EntityMapper<NiveauDTO, Niveau> {
	@Mapping(source = "typeStage.id", target = "typeStageId")
	@Mapping(source = "typeStage.libelle", target = "typeStageLibelle")
	NiveauDTO toDto(Niveau entity);

	@Mapping(source = "typeStageId", target = "typeStage.id")
	Niveau toEntity(NiveauDTO dto);
}
