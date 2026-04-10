package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entreprise d'accueil des stages.
 *
 * <p>Reference les encadreurs et les stages associes a cette structure.</p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String secteur;
}
