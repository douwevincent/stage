package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Encadreur;
import cm.univ.maroua.enspm.stage.repository.EncadreurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EncadreurService {

    private final EncadreurRepository encadreurRepository;

    public EncadreurService(EncadreurRepository encadreurRepository) {
        this.encadreurRepository = encadreurRepository;
    }

    public Page<Encadreur> findAll(Pageable pageable) {
        return encadreurRepository.findAll(pageable);
    }

    public Optional<Encadreur> findOne(Long id) {
        return encadreurRepository.findById(id);
    }

    public Encadreur save(Encadreur encadreur) {
        return encadreurRepository.save(encadreur);
    }

    public void delete(Long id) {
        encadreurRepository.deleteById(id);
    }
}
