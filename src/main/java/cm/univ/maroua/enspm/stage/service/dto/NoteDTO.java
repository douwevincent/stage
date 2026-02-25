package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record NoteDTO(Long id, SessionEvaluationDTO session, CritereDTO critere, Integer valeur, String commentaire)
        implements Serializable {
}
