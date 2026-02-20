package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "id_specialite")
    private Specialite specialite;

    @ManyToOne
    @JoinColumn(name = "id_niveau")
    private Niveau niveau;

    @OneToMany(mappedBy = "parcours")
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "parcours")
    private List<CritereParcours> critereParcours;
}
