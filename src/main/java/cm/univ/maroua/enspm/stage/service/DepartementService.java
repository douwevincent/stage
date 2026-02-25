package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Departement;
import cm.univ.maroua.enspm.stage.repository.DepartementRepository;
import cm.univ.maroua.enspm.stage.service.dto.DepartementDTO;
import cm.univ.maroua.enspm.stage.service.mapper.DepartementMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final DepartementMapper departementMapper;

    public DepartementService(DepartementRepository departementRepository, DepartementMapper departementMapper) {
        this.departementRepository = departementRepository;
        this.departementMapper = departementMapper;
    }

    public Page<DepartementDTO> findAll(Pageable pageable) {
        return departementRepository.findAll(pageable).map(departementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<DepartementDTO> findOne(Long id) {
        return departementRepository.findById(id).map(departementMapper::toDto);
    }

    public DepartementDTO save(DepartementDTO departementDTO) {
        Departement departement = departementMapper.toEntity(departementDTO);
        departement = departementRepository.save(departement);
        return departementMapper.toDto(departement);
    }

    public void delete(Long id) {
        departementRepository.deleteById(id);
    }
}
