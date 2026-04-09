package cm.univ.maroua.enspm.stage.domain;

/**
 * Barème d'évaluation des stages.
 *
 * <p>Un barème regroupe un ensemble de critères ({@link BaremeCritere}) pondérés par un
 * coefficient. Il peut être marqué comme barème par défaut ({@link #parDefaut}), auquel cas
 * il est automatiquement appliqué aux parcours qui n'en définissent pas de spécifique.</p>
 *
 * <p>L'unicité du barème par défaut est assurée par la colonne {@code par_defaut_unique_key},
 * dont la valeur est {@code "DEFAULT"} lorsque {@code parDefaut = true}, et {@code null}
 * sinon (permettant ainsi plusieurs barèmes non-défaut).</p>
 */
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "code" }),
    @UniqueConstraint(columnNames = { "par_defaut_unique_key" })
})
public class Bareme {

    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code métier unique du barème (ex. {@code "BAREME_LP"}). */
    @Column(nullable = false)
    private String code;

    /** Libellé descriptif du barème. */
    private String libelle;

    /** Indique si ce barème est actif et utilisable. */
    @Column(nullable = false)
    private Boolean actif = Boolean.TRUE;

    /** {@code true} si ce barème est le barème par défaut appliqué aux parcours sans barème propre. */
    @Column(name = "par_defaut", nullable = false)
    private Boolean parDefaut = Boolean.FALSE;

    /**
     * Clé technique pour garantir l'unicité du barème par défaut.
     * Vaut {@code "DEFAULT"} si {@code parDefaut = true}, {@code null} sinon.
     */
    @Column(name = "par_defaut_unique_key")
    private String parDefautUniqueKey;

    /**
     * Synchronise {@link #parDefautUniqueKey} avant chaque persistance ou mise à jour.
     * Positionne la clé à {@code "DEFAULT"} si ce barème est défini par défaut, {@code null} sinon.
     */
    @PrePersist
    @PreUpdate
    private void syncParDefautUniqueKey() {
        parDefautUniqueKey = Boolean.TRUE.equals(parDefaut) ? "DEFAULT" : null;
    }
}