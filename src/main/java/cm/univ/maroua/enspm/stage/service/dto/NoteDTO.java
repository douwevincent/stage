package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO NoteDTO.
 */
public record NoteDTO(Long id, Long sessionId, Long critereId, Long baremeCritereId, Integer valeur, String commentaire)
                implements Serializable {
}
