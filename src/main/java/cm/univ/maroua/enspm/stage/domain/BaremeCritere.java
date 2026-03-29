package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "bareme_id", "critere_id" }))
public class BaremeCritere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "bareme_id")
    private Bareme bareme;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "critere_id")
    private Critere critere;

    @NotNull
    private Float coefficient;
}