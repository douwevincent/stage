package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import cm.univ.maroua.enspm.stage.repository.EncadreurRepository;
import cm.univ.maroua.enspm.stage.service.dto.EncadreurDTO;
import cm.univ.maroua.enspm.stage.service.mapper.EncadreurMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EncadreurService {

    private final EncadreurRepository encadreurRepository;
    private final EncadreurMapper encadreurMapper;

    public EncadreurService(EncadreurRepository encadreurRepository, EncadreurMapper encadreurMapper) {
        this.encadreurRepository = encadreurRepository;
        this.encadreurMapper = encadreurMapper;
    }

    public Page<EncadreurDTO> findAll(Pageable pageable) {
        return encadreurRepository.findAll(pageable).map(encadreurMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EncadreurDTO> findOne(Long id) {
        return encadreurRepository.findById(id).map(encadreurMapper::toDto);
    }

    public EncadreurDTO save(EncadreurDTO encadreurDTO) {
        Encadreur encadreur = encadreurMapper.toEntity(encadreurDTO);
        encadreur = encadreurRepository.save(encadreur);
        return encadreurMapper.toDto(encadreur);
    }

    public void delete(Long id) {
        encadreurRepository.deleteById(id);
    }
}
