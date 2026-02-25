package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.NoteService;
import cm.univ.maroua.enspm.stage.service.dto.NoteDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public Page<NoteDTO> getAllNotes(Pageable pageable) {
        return noteService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNote(@PathVariable Long id) {
        return noteService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@Valid @RequestBody NoteDTO noteDTO) throws URISyntaxException {
        if (noteDTO.id() != null) {
            return ResponseEntity.badRequest().build();
        }
        NoteDTO result = noteService.save(noteDTO);
        return ResponseEntity.created(new URI("/notes/" + result.id()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody NoteDTO noteDTO) {
        if (noteDTO.id() == null || !id.equals(noteDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        NoteDTO result = noteService.save(noteDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
