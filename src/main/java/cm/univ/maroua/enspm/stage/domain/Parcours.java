package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "parcours")
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "parcours")
    private List<CritereParcours> critereParcours;
}
