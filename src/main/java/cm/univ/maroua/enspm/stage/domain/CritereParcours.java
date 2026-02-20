package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CritereParcours {

    @EmbeddedId
    private CritereParcoursId id;

    @ManyToOne
    @MapsId("idParcours")
    @JoinColumn(name = "id_parcours")
    private Parcours parcours;

    @ManyToOne
    @MapsId("idCritere")
    @JoinColumn(name = "id_critere")
    private Critere critere;

    private Float coefficient;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CritereParcoursId implements Serializable {
        private Long idParcours;
        private Long idCritere;
    }
}
