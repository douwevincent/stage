package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncadreurRepository extends JpaRepository<Encadreur, Long> {
}
