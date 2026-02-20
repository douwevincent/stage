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
    @JoinColumn(name = "id_session")
    private SessionEvaluation session;

    @ManyToOne
    @MapsId("idCritere")
    @JoinColumn(name = "id_critere")
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
