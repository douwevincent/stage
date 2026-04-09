package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Critere;
import cm.univ.maroua.enspm.stage.repository.CritereRepository;
import cm.univ.maroua.enspm.stage.repository.BaremeCritereRepository;
import cm.univ.maroua.enspm.stage.service.dto.CritereDTO;
import cm.univ.maroua.enspm.stage.service.mapper.CritereMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
/**
 * Service metier CritereService.
 */
public class CritereService {

    private final CritereRepository critereRepository;
    private final BaremeCritereRepository baremeCritereRepository;
    private final CritereMapper critereMapper;

    public CritereService(CritereRepository critereRepository, BaremeCritereRepository baremeCritereRepository, CritereMapper critereMapper) {
        this.critereRepository = critereRepository;
        this.baremeCritereRepository = baremeCritereRepository;
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
        // Vérifier si le critère est utilisé dans un barème
        if (baremeCritereRepository.existsByCritereId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Impossible de supprimer ce critère car il est utilisé dans un ou plusieurs barèmes");
        }
        critereRepository.deleteById(id);
    }
}
