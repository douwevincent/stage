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
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String secteur;

    @OneToMany(mappedBy = "entreprise")
    private List<Localite> localites;

    @OneToMany(mappedBy = "entreprise")
    private List<Encadreur> encadreurs;
}
