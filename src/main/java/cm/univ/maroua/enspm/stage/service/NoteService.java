package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Note;
import cm.univ.maroua.enspm.stage.repository.NoteRepository;
import cm.univ.maroua.enspm.stage.service.dto.NoteDTO;
import cm.univ.maroua.enspm.stage.service.mapper.NoteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    public Page<NoteDTO> findAll(Pageable pageable) {
        return noteRepository.findAll(pageable).map(noteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<NoteDTO> findOne(Long id) {
        return noteRepository.findById(id).map(noteMapper::toDto);
    }

    public NoteDTO save(NoteDTO noteDTO) {
        Note note = noteMapper.toEntity(noteDTO);
        note = noteRepository.save(note);
        return noteMapper.toDto(note);
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }
}
