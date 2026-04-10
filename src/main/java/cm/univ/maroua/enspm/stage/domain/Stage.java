package cm.univ.maroua.enspm.stage.domain;

/**
 * Entité centrale représentant un stage effectué par un étudiant dans une entreprise.
 *
 * <p>Un stage peut être déclaré par l'étudiant lui-même (source {@link Source#ETUDIANT},
 * statut initial {@link Statut#EN_ATTENTE_VALIDATION}) ou saisi directement par un opérateur
 * de scolarité (source {@link Source#OPERATEUR}, statut initial {@link Statut#VALIDE}).</p>
 *
 * <p>Une fois validé, un stage peut se voir associer une {@link SessionEvaluation} pour
 * permettre à l'encadreur d'évaluer l'étudiant via un code d'accès unique.</p>
 */
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
    /** Identifiant technique auto-généré. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Étudiant concerné par ce stage. Peut être {@code null} si non encore assigné. */
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    /** Entreprise d'accueil du stage. Obligatoire. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    /** Ville où se déroule le stage. */
    private String ville;
    /** Adresse précise du lieu de stage. */
    private String adresse;

    /** Encadreur professionnel au sein de l'entreprise. Peut être {@code null}. */
    @ManyToOne
    @JoinColumn(name = "encadreur_id")
    private Encadreur encadreur;

    /** Date de début effective du stage. */
    @NotNull
    private LocalDate dateDebut;

    /** Date de fin effective du stage. */
    @NotNull
    private LocalDate dateFin;

    /** Année académique à laquelle appartient ce stage. */
    @ManyToOne
    @JoinColumn(name = "annee_academique_id")
    private AnneeAcademique anneeAcademique;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "type_stage_id")
    private TypeStage typeStage;

    /** Origine de la déclaration du stage ({@link Source#ETUDIANT} ou {@link Source#OPERATEUR}). */
    @Enumerated(EnumType.STRING)
    private Source source;

    /** État courant du stage dans le workflow de validation. */
    @Enumerated(EnumType.STRING)
    private Statut statut;

    /** Chemin relatif vers le fichier d'autorisation de stage téléversé ({@code null} si absent). */
    private String cheminAutorisation;
}
