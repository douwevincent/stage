package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record NoteDTO(Long id, Long sessionId, Long critereId, Integer valeur, String commentaire)
                implements Serializable {
}
