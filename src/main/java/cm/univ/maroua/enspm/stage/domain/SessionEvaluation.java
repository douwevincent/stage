package cm.univ.maroua.enspm.stage.domain;

/**
 * Représente une session d'évaluation associée à un stage.
 *
 * <p>Lorsqu'une session est créée, un {@link #codeAcces} unique et confidentiel est généré.
 * Ce code est transmis à l'encadreur par e-mail afin qu'il puisse accéder au formulaire
 * d'évaluation public et saisir les notes sans nécessiter de compte utilisateur.</p>
 *
 * <p>Le cycle de vie d'une session suit l'énumération {@link SessionEvaluationStatut} :
 * {@code EN_ATTENTE} → {@code EN_COURS} → {@code TERMINEE}.</p>
 */
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionEvaluation {
    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stage auquel est rattachée cette session d'évaluation. */
    @OneToOne
    @JoinColumn(name = "stage_id")
    private Stage stage;

    /** Code d'accès confidentiel transmis à l'encadreur pour soumettre les évaluations. */
    private String codeAcces;
    /** Statut courant de la session ({@code EN_ATTENTE}, {@code EN_COURS}, {@code TERMINEE}). */
    @Enumerated(EnumType.STRING)
    private SessionEvaluationStatut statut = SessionEvaluationStatut.EN_ATTENTE;
    /** Date limite au-delà de laquelle la session ne peut plus être complétée. */
    private LocalDate dateLimite;

    /** Notes saisies dans le cadre de cette session d'évaluation. */
    @OneToMany(mappedBy = "session")
    private List<Note> notes;
}
