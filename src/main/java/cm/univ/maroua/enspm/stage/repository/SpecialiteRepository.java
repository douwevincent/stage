package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Specialite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    Page<Specialite> findAllByDepartementId(Long departementId, Pageable pageable);
}
