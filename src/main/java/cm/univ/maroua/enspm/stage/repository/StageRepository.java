package cm.univ.maroua.enspm.stage.repository;

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

    long countByAnneeAcademiqueId(Long anneeAcademiqueId);

    long countByAnneeAcademiqueIdAndStatut(Long anneeAcademiqueId, Statut statut);

    long countByAnneeAcademiqueIdAndEtudiantIsNull(Long anneeAcademiqueId);

    @Query("""
            SELECT COUNT(s) FROM Stage s
            WHERE s.anneeAcademique.id = :anneeAcademiqueId
              AND s.statut = cm.univ.maroua.enspm.stage.domain.Statut.VALIDE
              AND (
                  s.sessionEvaluation IS NULL
                  OR NOT EXISTS (
                      SELECT note FROM Note note
                      WHERE note.session = s.sessionEvaluation
                  )
              )
            """)
    long countStagesEnAttenteNotation(@Param("anneeAcademiqueId") Long anneeAcademiqueId);

    @Query("""
            SELECT COUNT(DISTINCT s.entreprise.id) FROM Stage s
            WHERE s.anneeAcademique.id = :anneeAcademiqueId
              AND s.entreprise IS NOT NULL
            """)
    long countDistinctEntreprisesByAnneeAcademiqueId(@Param("anneeAcademiqueId") Long anneeAcademiqueId);

    @Query("""
            SELECT s FROM Stage s
            WHERE s.statut = cm.univ.maroua.enspm.stage.domain.Statut.VALIDE
              AND s.encadreur IS NOT NULL
              AND s.encadreur.email IS NOT NULL
              AND s.typeStage.id = :typeStageId
              AND s.dateDebut = :referenceDate
              AND (
                  s.sessionEvaluation IS NULL
                  OR NOT EXISTS (
                      SELECT note FROM Note note
                      WHERE note.session = s.sessionEvaluation
                  )
              )
            """)
        List<Stage> findStagesNonNotesPourDebutStage(
            @Param("typeStageId") Long typeStageId,
          @Param("referenceDate") LocalDate referenceDate);

    @Query("""
            SELECT s FROM Stage s
          WHERE s.statut = cm.univ.maroua.enspm.stage.domain.Statut.VALIDE
            AND s.encadreur IS NOT NULL
            AND s.encadreur.email IS NOT NULL
            AND s.typeStage.id = :typeStageId
            AND s.dateFin = :referenceDate
              AND (
                  s.sessionEvaluation IS NULL
                  OR NOT EXISTS (
                      SELECT note FROM Note note
                      WHERE note.session = s.sessionEvaluation
                  )
              )
            """)
        List<Stage> findStagesNonNotesPourFinStage(
          @Param("typeStageId") Long typeStageId,
          @Param("referenceDate") LocalDate referenceDate);
}
