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
public class Critere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;
    private String categorie;

    @OneToMany(mappedBy = "critere")
    private List<CritereParcours> critereParcours;

    @OneToMany(mappedBy = "critere")
    private List<Note> notes;
}
