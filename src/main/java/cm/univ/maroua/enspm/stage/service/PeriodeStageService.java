package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.domain.PeriodeStage;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
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
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    public PeriodeStageService(PeriodeStageRepository periodeStageRepository, PeriodeStageMapper periodeStageMapper,
            AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.periodeStageRepository = periodeStageRepository;
        this.periodeStageMapper = periodeStageMapper;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    public Page<PeriodeStageDTO> findAll(Pageable pageable) {
        return periodeStageRepository.findAll(pageable).map(periodeStageMapper::toDto);
    }

    public Page<PeriodeStageDTO> findAllByAnneeAcademiqueId(Long anneeAcademiqueId, Pageable pageable) {
        return periodeStageRepository.findByAnneeAcademiqueId(anneeAcademiqueId, pageable)
                .map(periodeStageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PeriodeStageDTO> findOne(Long id) {
        return periodeStageRepository.findById(id).map(periodeStageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PeriodeStageDTO> findActiveByTypeStageId(Long typeStageId) {
        if (typeStageId == null) {
            return Optional.empty();
        }

        return anneeAcademiqueRepository.findByActifTrue()
                .flatMap(activeYear -> periodeStageRepository.findByTypeStageIdAndAnneeAcademiqueId(typeStageId, activeYear.getId()))
                .map(periodeStageMapper::toDto);
    }

    public PeriodeStageDTO save(PeriodeStageDTO periodeStageDTO) {
        PeriodeStage periodeStage = periodeStageMapper.toEntity(periodeStageDTO);
        if (periodeStage.getAnneeAcademique() == null || periodeStage.getAnneeAcademique().getId() == null) {
            AnneeAcademique active = anneeAcademiqueRepository.findByActifTrue()
                    .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));
            periodeStage.setAnneeAcademique(active);
        }
        periodeStage = periodeStageRepository.save(periodeStage);
        return periodeStageMapper.toDto(periodeStage);
    }

    public void delete(Long id) {
        periodeStageRepository.deleteById(id);
    }
}
