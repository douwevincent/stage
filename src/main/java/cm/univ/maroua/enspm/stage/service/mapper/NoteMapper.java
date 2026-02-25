package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Note;
import cm.univ.maroua.enspm.stage.service.dto.NoteDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SessionEvaluationMapper.class, CritereMapper.class})
public interface NoteMapper extends EntityMapper<NoteDTO, Note> {
}
