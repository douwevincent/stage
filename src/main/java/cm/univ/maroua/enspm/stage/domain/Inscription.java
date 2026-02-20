package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    @EmbeddedId
    private InscriptionId id;

    @ManyToOne
    @MapsId("idEtudiant")
    @JoinColumn(name = "id_etudiant")
    private Etudiant etudiant;

    @ManyToOne
    @MapsId("idParcours")
    @JoinColumn(name = "id_parcours")
    private Parcours parcours;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InscriptionId implements Serializable {
        private Long idEtudiant;
        private Long idParcours;
        private String anneeAcademique;
    }
}
