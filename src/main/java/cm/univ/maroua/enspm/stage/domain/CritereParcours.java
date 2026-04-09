package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Ancienne association entre un parcours et un critere avec coefficient.
 *
 * <p>Conservee pour compatibilite et migration des donnees historiques vers le
 * modele {@link Bareme}/{@link BaremeCritere}.</p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "parcours_id", "critere_id" }))
public class CritereParcours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parcours_id")
    private Parcours parcours;

    @ManyToOne
    @JoinColumn(name = "critere_id")
    private Critere critere;

    private Float coefficient;

}
