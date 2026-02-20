package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
}
