package cm.univ.maroua.enspm.stage.domain;

/**
 * Représente un étudiant inscrit dans l'établissement.
 *
 * <p>L'étudiant est identifié de manière unique par son {@link #matricule}.
 * Il peut être inscrit dans plusieurs parcours via {@link Inscription} et réaliser
 * plusieurs stages au fil de ses années académiques.</p>
 */
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {
    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom complet de l'étudiant. */
    @NotNull
    private String nom;

    /** Adresse e-mail de contact de l'étudiant. Peut être {@code null}. */
    private String email;

    /** Numéro de téléphone de contact. Peut être {@code null}. */
    private String telephone;

    /** Matricule universitaire unique de l'étudiant (ex. {@code "22B012"}). */
    @NotNull
    @Column(unique = true)
    private String matricule;
}
