package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Etudiant;
import cm.univ.maroua.enspm.stage.repository.EtudiantRepository;
import cm.univ.maroua.enspm.stage.service.dto.EtudiantDTO;
import cm.univ.maroua.enspm.stage.service.mapper.EtudiantMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final EtudiantMapper etudiantMapper;

    public EtudiantService(EtudiantRepository etudiantRepository, EtudiantMapper etudiantMapper) {
        this.etudiantRepository = etudiantRepository;
        this.etudiantMapper = etudiantMapper;
    }

    public Page<EtudiantDTO> findAll(Pageable pageable) {
        return etudiantRepository.findAll(pageable).map(etudiantMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EtudiantDTO> findOne(Long id) {
        return etudiantRepository.findById(id).map(etudiantMapper::toDto);
    }

    public EtudiantDTO save(EtudiantDTO etudiantDTO) {
        Etudiant etudiant = etudiantMapper.toEntity(etudiantDTO);
        etudiant = etudiantRepository.save(etudiant);
        return etudiantMapper.toDto(etudiant);
    }

    public void delete(Long id) {
        etudiantRepository.deleteById(id);
    }
}
