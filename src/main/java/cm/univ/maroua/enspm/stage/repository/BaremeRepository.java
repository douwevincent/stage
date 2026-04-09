package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Bareme;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Repository JPA BaremeRepository.
 */
public interface BaremeRepository extends JpaRepository<Bareme, Long> {

    Optional<Bareme> findByParDefautTrueAndActifTrue();

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update Bareme b set b.parDefaut = false, b.parDefautUniqueKey = null where b.parDefaut = true")
	void clearDefaultBareme();
}
