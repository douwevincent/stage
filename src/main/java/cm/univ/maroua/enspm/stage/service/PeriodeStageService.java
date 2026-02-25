package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.repository.PeriodeStageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PeriodeStageService {

    private final PeriodeStageRepository periodeStageRepository;

    public PeriodeStageService(PeriodeStageRepository periodeStageRepository) {
        this.periodeStageRepository = periodeStageRepository;
    }

    public Page<PeriodeStage> findAll(Pageable pageable) {
        return periodeStageRepository.findAll(pageable);
    }

    public Optional<PeriodeStage> findOne(Long id) {
        return periodeStageRepository.findById(id);
    }

    public PeriodeStage save(PeriodeStage periodeStage) {
        return periodeStageRepository.save(periodeStage);
    }

    public void delete(Long id) {
        periodeStageRepository.deleteById(id);
    }
}
