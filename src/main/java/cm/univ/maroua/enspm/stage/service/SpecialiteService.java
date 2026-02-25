package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.repository.SpecialiteRepository;
import cm.univ.maroua.enspm.stage.service.dto.SpecialiteDTO;
import cm.univ.maroua.enspm.stage.service.mapper.SpecialiteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;
    private final SpecialiteMapper specialiteMapper;

    public SpecialiteService(SpecialiteRepository specialiteRepository, SpecialiteMapper specialiteMapper) {
        this.specialiteRepository = specialiteRepository;
        this.specialiteMapper = specialiteMapper;
    }

    public Page<SpecialiteDTO> findAll(Pageable pageable) {
        return specialiteRepository.findAll(pageable).map(specialiteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<SpecialiteDTO> findOne(Long id) {
        return specialiteRepository.findById(id).map(specialiteMapper::toDto);
    }

    public SpecialiteDTO save(SpecialiteDTO specialiteDTO) {
        Specialite specialite = specialiteMapper.toEntity(specialiteDTO);
        specialite = specialiteRepository.save(specialite);
        return specialiteMapper.toDto(specialite);
    }

    public void delete(Long id) {
        specialiteRepository.deleteById(id);
    }
}
