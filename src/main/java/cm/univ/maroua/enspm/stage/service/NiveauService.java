package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Niveau;
import cm.univ.maroua.enspm.stage.repository.NiveauRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NiveauService {

    private final NiveauRepository niveauRepository;

    public NiveauService(NiveauRepository niveauRepository) {
        this.niveauRepository = niveauRepository;
    }

    public Page<Niveau> findAll(Pageable pageable) {
        return niveauRepository.findAll(pageable);
    }

    public Optional<Niveau> findOne(Long id) {
        return niveauRepository.findById(id);
    }

    public Niveau save(Niveau niveau) {
        return niveauRepository.save(niveau);
    }

    public void delete(Long id) {
        niveauRepository.deleteById(id);
    }
}
