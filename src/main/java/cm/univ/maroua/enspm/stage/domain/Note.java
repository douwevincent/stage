package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @EmbeddedId
    private NoteId id;

    @ManyToOne
    @MapsId("idSession")
    @JoinColumn(name = "session_evaluation_id")
    private SessionEvaluation session;

    @ManyToOne
    @MapsId("idCritere")
    @JoinColumn(name = "critere_id")
    private Critere critere;

    private Integer valeur;
    private String commentaire;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteId implements Serializable {
        private Long idSession;
        private Long idCritere;
    }
}
