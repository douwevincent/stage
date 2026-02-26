package cm.univ.maroua.enspm.stage.service.mapper;

import cm.univ.maroua.enspm.stage.domain.Note;
import cm.univ.maroua.enspm.stage.service.dto.NoteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper extends EntityMapper<NoteDTO, Note> {
    @Mapping(source = "session.id", target = "sessionId")
    @Mapping(source = "critere.id", target = "critereId")
    NoteDTO toDto(Note entity);

    @Mapping(source = "sessionId", target = "session.id")
    @Mapping(source = "critereId", target = "critere.id")
    Note toEntity(NoteDTO dto);
}
