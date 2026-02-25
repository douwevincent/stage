package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.repository.TypeStageRepository;
import cm.univ.maroua.enspm.stage.service.dto.TypeStageDTO;
import cm.univ.maroua.enspm.stage.service.mapper.TypeStageMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TypeStageService {

    private final TypeStageRepository typeStageRepository;
    private final TypeStageMapper typeStageMapper;

    public TypeStageService(TypeStageRepository typeStageRepository, TypeStageMapper typeStageMapper) {
        this.typeStageRepository = typeStageRepository;
        this.typeStageMapper = typeStageMapper;
    }

    public Page<TypeStageDTO> findAll(Pageable pageable) {
        return typeStageRepository.findAll(pageable).map(typeStageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<TypeStageDTO> findOne(Long id) {
        return typeStageRepository.findById(id).map(typeStageMapper::toDto);
    }

    public TypeStageDTO save(TypeStageDTO typeStageDTO) {
        TypeStage typeStage = typeStageMapper.toEntity(typeStageDTO);
        typeStage = typeStageRepository.save(typeStage);
        return typeStageMapper.toDto(typeStage);
    }

    public void delete(Long id) {
        typeStageRepository.deleteById(id);
    }
}
