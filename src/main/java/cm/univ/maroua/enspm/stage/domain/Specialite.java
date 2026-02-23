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
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @OneToMany(mappedBy = "specialite")
    private List<Parcours> parcours;
}
