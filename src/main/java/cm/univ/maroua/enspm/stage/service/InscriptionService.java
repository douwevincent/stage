package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.service.dto.InscriptionDTO;
import cm.univ.maroua.enspm.stage.service.mapper.InscriptionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final InscriptionMapper inscriptionMapper;

    public InscriptionService(InscriptionRepository inscriptionRepository, InscriptionMapper inscriptionMapper) {
        this.inscriptionRepository = inscriptionRepository;
        this.inscriptionMapper = inscriptionMapper;
    }

    public Page<InscriptionDTO> findAll(Pageable pageable) {
        return inscriptionRepository.findAll(pageable).map(inscriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<InscriptionDTO> findOne(Long id) {
        return inscriptionRepository.findById(id).map(inscriptionMapper::toDto);
    }

    public InscriptionDTO save(InscriptionDTO inscriptionDTO) {
        Inscription inscription = inscriptionMapper.toEntity(inscriptionDTO);
        inscription = inscriptionRepository.save(inscription);
        return inscriptionMapper.toDto(inscription);
    }

    public void delete(Long id) {
        inscriptionRepository.deleteById(id);
    }
}
