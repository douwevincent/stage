package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.repository.NiveauRepository;
import cm.univ.maroua.enspm.stage.service.dto.NiveauDTO;
import cm.univ.maroua.enspm.stage.service.mapper.NiveauMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NiveauService {

    private final NiveauRepository niveauRepository;
    private final NiveauMapper niveauMapper;

    public NiveauService(NiveauRepository niveauRepository, NiveauMapper niveauMapper) {
        this.niveauRepository = niveauRepository;
        this.niveauMapper = niveauMapper;
    }

    public Page<NiveauDTO> findAll(Pageable pageable) {
        return niveauRepository.findAll(pageable).map(niveauMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<NiveauDTO> findOne(Long id) {
        return niveauRepository.findById(id).map(niveauMapper::toDto);
    }

    public NiveauDTO save(NiveauDTO niveauDTO) {
        Niveau niveau = niveauMapper.toEntity(niveauDTO);
        niveau = niveauRepository.save(niveau);
        return niveauMapper.toDto(niveau);
    }

    public void delete(Long id) {
        niveauRepository.deleteById(id);
    }
}
