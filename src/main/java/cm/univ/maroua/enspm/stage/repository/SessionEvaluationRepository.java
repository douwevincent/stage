package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.SessionEvaluation;
import cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repository JPA SessionEvaluationRepository.
 */
public interface SessionEvaluationRepository extends JpaRepository<SessionEvaluation, Long> {
    Optional<SessionEvaluation> findFirstByCodeAcces(String codeAcces);

    Optional<SessionEvaluation> findByStageId(Long stageId);

    List<SessionEvaluation> findByStageIdIn(Collection<Long> stageIds);

    List<SessionEvaluation> findByStageEncadreurIdOrderByStageDateDebutAsc(Long encadreurId);

    Page<SessionEvaluation> findByStatut(SessionEvaluationStatut statut, Pageable pageable);

        @Query("""
                        select se from SessionEvaluation se
                        join se.stage st
                        left join st.etudiant et
                        where se.statut = cm.univ.maroua.enspm.stage.domain.SessionEvaluationStatut.TERMINEE
                            and (
                                :q is null
                                or trim(:q) = ''
                                or lower(et.nom) like lower(concat('%', :q, '%'))
                                or lower(et.matricule) like lower(concat('%', :q, '%'))
                            )
                            and exists (
                                select i.id
                                from Inscription i
                                where i.etudiant.id = et.id
                                    and i.anneeAcademique.id = :anneeAcademiqueId
                                    and (:niveauId is null or i.parcours.niveau.id = :niveauId)
                                    and (:departementId is null or i.parcours.specialite.departement.id = :departementId)
                                    and (:specialiteId is null or i.parcours.specialite.id = :specialiteId)
                            )
                        """)
        Page<SessionEvaluation> searchEvaluatedByFilters(
                        @Param("anneeAcademiqueId") Long anneeAcademiqueId,
                        @Param("niveauId") Long niveauId,
                        @Param("departementId") Long departementId,
                        @Param("specialiteId") Long specialiteId,
                        @Param("q") String q,
                        Pageable pageable);
}
