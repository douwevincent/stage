package cm.univ.maroua.enspm.stage.domain;

/**
 * Association entre un {@link Bareme} et un {@link Critere}, avec un coefficient de pondération.
 *
 * <p>La contrainte d'unicité {@code (bareme_id, critere_id)} garantit qu'un critère donné
 * n'est inclus qu'une seule fois dans un barème. Le coefficient doit être compris entre
 * {@code 0.0} et {@code 20.0}.</p>
 */
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "bareme_id", "critere_id" }))
public class BaremeCritere {

    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Barème auquel appartient cette ligne de pondération. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "bareme_id")
    private Bareme bareme;

    /** Critère d'évaluation pondéré. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "critere_id")
    private Critere critere;

    /** Coefficient de pondération du critère dans ce barème (entre 0 et 20 inclus). */
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("20.0")
    private Float coefficient;
}