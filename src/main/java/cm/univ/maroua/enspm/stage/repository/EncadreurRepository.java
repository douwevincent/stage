package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository JPA EncadreurRepository.
 */
public interface EncadreurRepository extends JpaRepository<Encadreur, Long> {
		@Query("""
						select e from Encadreur e
						where (:entrepriseId is null or e.entreprise.id = :entrepriseId)
							and (
								:q = ''
								or lower(e.nom) like lower(concat('%', :q, '%'))
								or lower(e.email) like lower(concat('%', :q, '%'))
							)
						""")
		Page<Encadreur> search(@Param("entrepriseId") Long entrepriseId, @Param("q") String q, Pageable pageable);
}
