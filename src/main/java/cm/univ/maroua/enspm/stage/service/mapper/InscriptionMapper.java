package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.service.dto.InscriptionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AnneeAcademiqueMapper.class, EtudiantMapper.class, ParcoursMapper.class})
public interface InscriptionMapper extends EntityMapper<InscriptionDTO, Inscription> {
}
