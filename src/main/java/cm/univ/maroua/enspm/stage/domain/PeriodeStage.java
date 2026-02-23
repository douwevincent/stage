package cm.univ.maroua.enspm.stage.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "type_stage_id", "annee_academique_id" })
})
public class PeriodeStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "type_stage_id")
    private TypeStage typeStage;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "annee_academique_id")
    private AnneeAcademique anneeAcademique;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;
}
