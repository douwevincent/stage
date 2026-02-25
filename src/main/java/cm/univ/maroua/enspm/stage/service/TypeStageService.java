package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.repository.TypeStageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TypeStageService {

    private final TypeStageRepository typeStageRepository;

    public TypeStageService(TypeStageRepository typeStageRepository) {
        this.typeStageRepository = typeStageRepository;
    }

    public Page<TypeStage> findAll(Pageable pageable) {
        return typeStageRepository.findAll(pageable);
    }

    public Optional<TypeStage> findOne(Long id) {
        return typeStageRepository.findById(id);
    }

    public TypeStage save(TypeStage typeStage) {
        return typeStageRepository.save(typeStage);
    }

    public void delete(Long id) {
        typeStageRepository.deleteById(id);
    }
}
