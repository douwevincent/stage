package cm.univ.maroua.enspm.stage.repository;

import cm.univ.maroua.enspm.stage.domain.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repository JPA AppSettingRepository.
 */
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    List<AppSetting> findAllByOrderByCleAsc();

    Optional<AppSetting> findByCle(String cle);
}
