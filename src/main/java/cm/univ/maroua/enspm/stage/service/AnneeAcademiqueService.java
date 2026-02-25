package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.service.dto.AnneeAcademiqueDTO;
import cm.univ.maroua.enspm.stage.service.mapper.AnneeAcademiqueMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final AnneeAcademiqueMapper anneeAcademiqueMapper;

    public AnneeAcademiqueService(AnneeAcademiqueRepository anneeAcademiqueRepository,
            AnneeAcademiqueMapper anneeAcademiqueMapper) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.anneeAcademiqueMapper = anneeAcademiqueMapper;
    }

    public Page<AnneeAcademiqueDTO> findAll(Pageable pageable) {
        return anneeAcademiqueRepository.findAll(pageable).map(anneeAcademiqueMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<AnneeAcademiqueDTO> findOne(Long id) {
        return anneeAcademiqueRepository.findById(id).map(anneeAcademiqueMapper::toDto);
    }

    public AnneeAcademiqueDTO save(AnneeAcademiqueDTO anneeAcademiqueDTO) {
        AnneeAcademique anneeAcademique = anneeAcademiqueMapper.toEntity(anneeAcademiqueDTO);
        anneeAcademique = anneeAcademiqueRepository.save(anneeAcademique);
        return anneeAcademiqueMapper.toDto(anneeAcademique);
    }

    public void delete(Long id) {
        anneeAcademiqueRepository.deleteById(id);
    }
}
