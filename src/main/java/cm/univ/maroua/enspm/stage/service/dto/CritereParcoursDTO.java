package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record CritereParcoursDTO(Long id, ParcoursDTO parcours, CritereDTO critere, Float coefficient)
        implements Serializable {
}
