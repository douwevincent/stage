package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.CritereParcours;
import cm.univ.maroua.enspm.stage.repository.CritereParcoursRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CritereParcoursService {

    private final CritereParcoursRepository critereParcoursRepository;

    public CritereParcoursService(CritereParcoursRepository critereParcoursRepository) {
        this.critereParcoursRepository = critereParcoursRepository;
    }

    public Page<CritereParcours> findAll(Pageable pageable) {
        return critereParcoursRepository.findAll(pageable);
    }

    public Optional<CritereParcours> findOne(Long id) {
        return critereParcoursRepository.findById(id);
    }

    public CritereParcours save(CritereParcours critereParcours) {
        return critereParcoursRepository.save(critereParcours);
    }

    public void delete(Long id) {
        critereParcoursRepository.deleteById(id);
    }
}
