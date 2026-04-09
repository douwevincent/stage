package cm.univ.maroua.enspm.stage.domain;

/**
 * Représente une année académique dans le système de gestion des stages.
 *
 * <p>Une seule année peut être active à la fois (voir {@link #actif}).
 * L'activation se fait via {@link cm.univ.maroua.enspm.stage.service.AnneeAcademiqueService#activate(Long)},
 * qui désactive toutes les autres avant de marquer la cible comme active.</p>
 */
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnneeAcademique {
    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Libellé de l'année académique, ex. {@code "2024-2025"}. */
    @NotNull
    private String libelle;

    /**
     * Indique si cette année académique est l'année courante active.
     * Une seule instance peut avoir {@code actif = true} à un instant donné.
     */
    @Column(nullable = false)
    private boolean actif = false;
}
