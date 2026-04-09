package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Niveau academique (ex. L1, L2, M1) d'un parcours.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Niveau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "type_stage_id")
    private TypeStage typeStage;

    @OneToMany(mappedBy = "niveau")
    private List<Parcours> parcours;
}
