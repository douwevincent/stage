package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Specialite;
import cm.univ.maroua.enspm.stage.repository.SpecialiteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    public SpecialiteService(SpecialiteRepository specialiteRepository) {
        this.specialiteRepository = specialiteRepository;
    }

    public Page<Specialite> findAll(Pageable pageable) {
        return specialiteRepository.findAll(pageable);
    }

    public Optional<Specialite> findOne(Long id) {
        return specialiteRepository.findById(id);
    }

    public Specialite save(Specialite specialite) {
        return specialiteRepository.save(specialite);
    }

    public void delete(Long id) {
        specialiteRepository.deleteById(id);
    }
}
