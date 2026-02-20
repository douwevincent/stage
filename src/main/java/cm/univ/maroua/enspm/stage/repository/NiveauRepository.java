package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long> {
}
