package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

/**
 * DTO BaremeCritereDTO.
 */
public record BaremeCritereDTO(
        Long id,
        Long baremeId,
        String baremeCode,
        Long critereId,
        String critereLibelle,
        Float coefficient) implements Serializable {
}
