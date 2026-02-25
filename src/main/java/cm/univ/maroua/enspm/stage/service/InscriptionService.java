package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository) {
        this.inscriptionRepository = inscriptionRepository;
    }

    public Page<Inscription> findAll(Pageable pageable) {
        return inscriptionRepository.findAll(pageable);
    }

    public Optional<Inscription> findOne(Long id) {
        return inscriptionRepository.findById(id);
    }

    public Inscription save(Inscription inscription) {
        return inscriptionRepository.save(inscription);
    }

    public void delete(Long id) {
        inscriptionRepository.deleteById(id);
    }
}
