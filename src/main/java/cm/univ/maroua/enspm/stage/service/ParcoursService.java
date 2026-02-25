package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;

    public ParcoursService(ParcoursRepository parcoursRepository) {
        this.parcoursRepository = parcoursRepository;
    }

    public Page<Parcours> findAll(Pageable pageable) {
        return parcoursRepository.findAll(pageable);
    }

    public Optional<Parcours> findOne(Long id) {
        return parcoursRepository.findById(id);
    }

    public Parcours save(Parcours parcours) {
        return parcoursRepository.save(parcours);
    }

    public void delete(Long id) {
        parcoursRepository.deleteById(id);
    }
}
