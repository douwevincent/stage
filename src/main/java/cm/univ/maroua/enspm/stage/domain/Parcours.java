package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Parcours de formation combinant une specialite et un niveau.
 *
 * <p>Un parcours peut utiliser un bareme specifique ou heriter du bareme
 * par defaut de l'application.</p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parcours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "niveau_id")
    private Niveau niveau;

    @ManyToOne
    @JoinColumn(name = "bareme_id")
    private Bareme bareme;

    @OneToMany(mappedBy = "parcours")
    private List<Inscription> inscriptions;
}
