package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record CritereParcoursDTO(Long id, Long parcoursId, Long critereId, Float coefficient)
                implements Serializable {
}
