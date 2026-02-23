package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "annee_academique_id", "etudiant_id", "parcours_id" }))
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "annee_academique_id")
    private AnneeAcademique anneeAcademique;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "parcours_id")
    private Parcours parcours;
}
