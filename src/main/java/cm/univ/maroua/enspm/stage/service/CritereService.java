package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Critere;
import cm.univ.maroua.enspm.stage.repository.CritereRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CritereService {

    private final CritereRepository critereRepository;

    public CritereService(CritereRepository critereRepository) {
        this.critereRepository = critereRepository;
    }

    public Page<Critere> findAll(Pageable pageable) {
        return critereRepository.findAll(pageable);
    }

    public Optional<Critere> findOne(Long id) {
        return critereRepository.findById(id);
    }

    public Critere save(Critere critere) {
        return critereRepository.save(critere);
    }

    public void delete(Long id) {
        critereRepository.deleteById(id);
    }
}
