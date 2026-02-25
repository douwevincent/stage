package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.repository.PeriodeStageRepository;
import cm.univ.maroua.enspm.stage.service.dto.PeriodeStageDTO;
import cm.univ.maroua.enspm.stage.service.mapper.PeriodeStageMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PeriodeStageService {

    private final PeriodeStageRepository periodeStageRepository;
    private final PeriodeStageMapper periodeStageMapper;

    public PeriodeStageService(PeriodeStageRepository periodeStageRepository, PeriodeStageMapper periodeStageMapper) {
        this.periodeStageRepository = periodeStageRepository;
        this.periodeStageMapper = periodeStageMapper;
    }

    public Page<PeriodeStageDTO> findAll(Pageable pageable) {
        return periodeStageRepository.findAll(pageable).map(periodeStageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PeriodeStageDTO> findOne(Long id) {
        return periodeStageRepository.findById(id).map(periodeStageMapper::toDto);
    }

    public PeriodeStageDTO save(PeriodeStageDTO periodeStageDTO) {
        PeriodeStage periodeStage = periodeStageMapper.toEntity(periodeStageDTO);
        periodeStage = periodeStageRepository.save(periodeStage);
        return periodeStageMapper.toDto(periodeStage);
    }

    public void delete(Long id) {
        periodeStageRepository.deleteById(id);
    }
}
