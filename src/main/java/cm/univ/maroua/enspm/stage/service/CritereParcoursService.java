package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import cm.univ.maroua.enspm.stage.repository.CritereParcoursRepository;
import cm.univ.maroua.enspm.stage.service.dto.CritereParcoursDTO;
import cm.univ.maroua.enspm.stage.service.mapper.CritereParcoursMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CritereParcoursService {

    private final CritereParcoursRepository critereParcoursRepository;
    private final CritereParcoursMapper critereParcoursMapper;

    public CritereParcoursService(CritereParcoursRepository critereParcoursRepository,
            CritereParcoursMapper critereParcoursMapper) {
        this.critereParcoursRepository = critereParcoursRepository;
        this.critereParcoursMapper = critereParcoursMapper;
    }

    public Page<CritereParcoursDTO> findAll(Pageable pageable) {
        return critereParcoursRepository.findAll(pageable).map(critereParcoursMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<CritereParcoursDTO> findOne(Long id) {
        return critereParcoursRepository.findById(id).map(critereParcoursMapper::toDto);
    }

    public CritereParcoursDTO save(CritereParcoursDTO critereParcoursDTO) {
        CritereParcours critereParcours = critereParcoursMapper.toEntity(critereParcoursDTO);
        critereParcours = critereParcoursRepository.save(critereParcours);
        return critereParcoursMapper.toDto(critereParcours);
    }

    public void delete(Long id) {
        critereParcoursRepository.deleteById(id);
    }
}
