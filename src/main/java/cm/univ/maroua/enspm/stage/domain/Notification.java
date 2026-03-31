package cm.univ.maroua.enspm.stage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "type_stage_id", "reference_date_type", "offset_days" })
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "type_stage_id")
    private TypeStage typeStage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_date_type")
    private NotificationReferenceDateType referenceDateType;

    @NotNull
    @Min(-365)
    @Max(365)
    @Column(name = "offset_days")
    private Integer offsetDays;

    @NotNull
    private Boolean actif = true;
}
