package cm.univ.maroua.enspm.stage.domain;

/**
 * Représente l'inscription d'un étudiant à un parcours pour une année académique donnée.
 *
 * <p>La contrainte d'unicité {@code (annee_academique_id, etudiant_id, parcours_id)} garantit
 * qu'un étudiant ne peut être inscrit qu'une seule fois au même parcours pour la même année.</p>
 */
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "annee_academique_id", "etudiant_id", "parcours_id" }))
public class Inscription {

    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Année académique de l'inscription. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "annee_academique_id")
    private AnneeAcademique anneeAcademique;

    /** Étudiant inscrit. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    /** Parcours (spécialité + niveau) dans lequel l'étudiant est inscrit. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "parcours_id")
    private Parcours parcours;
}
