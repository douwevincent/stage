package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<AnneeAcademique, Long> {
}
