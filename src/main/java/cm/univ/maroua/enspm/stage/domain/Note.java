package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_evaluation_id")
    private SessionEvaluation session;

    @ManyToOne
    @JoinColumn(name = "critere_id")
    private Critere critere;

    @ManyToOne
    @JoinColumn(name = "bareme_critere_id")
    private BaremeCritere baremeCritere;

    private Integer valeur;
    private String commentaire;
}
