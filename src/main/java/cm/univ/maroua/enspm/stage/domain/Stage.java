package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_etudiant")
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "id_localite")
    private Localite localite;

    @ManyToOne
    @JoinColumn(name = "id_encadreur")
    private Encadreur encadreur;

    private String typeStage;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String anneeAcademique;

    @OneToOne(mappedBy = "stage")
    private SessionEvaluation sessionEvaluation;
}
