package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Etudiant;
import cm.univ.maroua.enspm.stage.repository.EtudiantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public Page<Etudiant> findAll(Pageable pageable) {
        return etudiantRepository.findAll(pageable);
    }

    public Optional<Etudiant> findOne(Long id) {
        return etudiantRepository.findById(id);
    }

    public Etudiant save(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public void delete(Long id) {
        etudiantRepository.deleteById(id);
    }
}
