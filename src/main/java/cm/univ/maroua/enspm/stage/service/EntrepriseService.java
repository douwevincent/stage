package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Entreprise;
import cm.univ.maroua.enspm.stage.repository.EntrepriseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    public EntrepriseService(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    public Page<Entreprise> findAll(Pageable pageable) {
        return entrepriseRepository.findAll(pageable);
    }

    public Optional<Entreprise> findOne(Long id) {
        return entrepriseRepository.findById(id);
    }

    public Entreprise save(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    public void delete(Long id) {
        entrepriseRepository.deleteById(id);
    }
}
