package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.domain.Statut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    Page<Stage> findByStatut(Statut statut, Pageable pageable);

    /**
     * Retourne les encadreurs distincts (avec email) ayant au moins un stage VALIDE
     * pour le type de stage de la période donnée, et dont au moins un stage
     * n'est pas encore noté (session d'évaluation absente ou sans notes).
     *
     * La liaison typeStage se fait via : Stage.etudiant → Inscription → Parcours → Niveau.typeStage
     * en s'assurant que l'Inscription partage la même annéeAcadémique que le Stage.
     */
    @Query("""
            SELECT DISTINCT s.encadreur FROM Stage s
            WHERE s.statut = cm.univ.maroua.enspm.stage.domain.Statut.VALIDE
              AND s.encadreur IS NOT NULL
              AND s.encadreur.email IS NOT NULL
              AND s.anneeAcademique.id = :anneeAcademiqueId
              AND s.dateDebut <= :periodeFin
              AND s.dateFin >= :periodeDebut
              AND EXISTS (
                  SELECT i FROM Inscription i
                  JOIN i.parcours p
                  JOIN p.niveau n
                  WHERE i.etudiant = s.etudiant
                    AND i.anneeAcademique = s.anneeAcademique
                    AND n.typeStage.id = :typeStageId
              )
              AND (
                  s.sessionEvaluation IS NULL
                  OR NOT EXISTS (
                      SELECT note FROM Note note
                      WHERE note.session = s.sessionEvaluation
                  )
              )
            """)
    List<Encadreur> findEncadreursAvecStagesNonNotesPourPeriode(
            @Param("typeStageId") Long typeStageId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId,
            @Param("periodeDebut") LocalDate periodeDebut,
            @Param("periodeFin") LocalDate periodeFin);
}
