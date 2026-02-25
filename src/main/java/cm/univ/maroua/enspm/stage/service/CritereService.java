package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Critere;
import cm.univ.maroua.enspm.stage.repository.CritereRepository;
import cm.univ.maroua.enspm.stage.service.dto.CritereDTO;
import cm.univ.maroua.enspm.stage.service.mapper.CritereMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CritereService {

    private final CritereRepository critereRepository;
    private final CritereMapper critereMapper;

    public CritereService(CritereRepository critereRepository, CritereMapper critereMapper) {
        this.critereRepository = critereRepository;
        this.critereMapper = critereMapper;
    }

    public Page<CritereDTO> findAll(Pageable pageable) {
        return critereRepository.findAll(pageable).map(critereMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<CritereDTO> findOne(Long id) {
        return critereRepository.findById(id).map(critereMapper::toDto);
    }

    public CritereDTO save(CritereDTO critereDTO) {
        Critere critere = critereMapper.toEntity(critereDTO);
        critere = critereRepository.save(critere);
        return critereMapper.toDto(critere);
    }

    public void delete(Long id) {
        critereRepository.deleteById(id);
    }
}
