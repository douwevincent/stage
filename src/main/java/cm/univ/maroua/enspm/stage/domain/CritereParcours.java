package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "id_parcours", "id_critere" }))
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
