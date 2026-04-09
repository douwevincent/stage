package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    private String ville;
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "encadreur_id")
    private Encadreur encadreur;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name = "annee_academique_id")
    private AnneeAcademique anneeAcademique;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "type_stage_id")
    private TypeStage typeStage;

    @OneToOne(mappedBy = "stage")
    private SessionEvaluation sessionEvaluation;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    private String cheminAutorisation;
}
