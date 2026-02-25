package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.repository.StageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StageService {

    private final StageRepository stageRepository;

    public StageService(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    public Page<Stage> findAll(Pageable pageable) {
        return stageRepository.findAll(pageable);
    }

    public Optional<Stage> findOne(Long id) {
        return stageRepository.findById(id);
    }

    public Stage save(Stage stage) {
        return stageRepository.save(stage);
    }

    public void delete(Long id) {
        stageRepository.deleteById(id);
    }
}
