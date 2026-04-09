package cm.univ.maroua.enspm.stage.domain;

/**
 * Critère d'évaluation utilisé dans les barèmes de notation des stages.
 *
 * <p>Un critère est défini par un libellé et appartient optionnellement à une catégorie
 * (ex. {@code "Comportement professionnel"}, {@code "Compétences techniques"}).
 * Il peut être associé à plusieurs barèmes via {@link BaremeCritere}.</p>
 */
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Critere {
    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Intitulé du critère d'évaluation (ex. {@code "Ponctualité"}). */
    private String libelle;
    /** Catégorie de regroupement du critère (ex. {@code "Comportement professionnel"}). */
    private String categorie;

    /** Associations de ce critère aux différents barèmes avec leurs coefficients respectifs. */
    @OneToMany(mappedBy = "critere")
    private List<BaremeCritere> baremeCriteres;

    /** Notes attribuées pour ce critère dans les sessions d'évaluation. */
    @OneToMany(mappedBy = "critere")
    private List<Note> notes;
}
