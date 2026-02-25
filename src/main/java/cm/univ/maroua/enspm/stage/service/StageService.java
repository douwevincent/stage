package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Stage;
import cm.univ.maroua.enspm.stage.repository.StageRepository;
import cm.univ.maroua.enspm.stage.service.dto.StageDTO;
import cm.univ.maroua.enspm.stage.service.mapper.StageMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StageService {

    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    public StageService(StageRepository stageRepository, StageMapper stageMapper) {
        this.stageRepository = stageRepository;
        this.stageMapper = stageMapper;
    }

    public Page<StageDTO> findAll(Pageable pageable) {
        return stageRepository.findAll(pageable).map(stageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StageDTO> findOne(Long id) {
        return stageRepository.findById(id).map(stageMapper::toDto);
    }

    public StageDTO save(StageDTO stageDTO) {
        Stage stage = stageMapper.toEntity(stageDTO);
        stage = stageRepository.save(stage);
        return stageMapper.toDto(stage);
    }

    public void delete(Long id) {
        stageRepository.deleteById(id);
    }
}
