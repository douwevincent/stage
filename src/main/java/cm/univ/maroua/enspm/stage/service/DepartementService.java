package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Departement;
import cm.univ.maroua.enspm.stage.repository.DepartementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DepartementService {

    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    public Page<Departement> findAll(Pageable pageable) {
        return departementRepository.findAll(pageable);
    }

    public Optional<Departement> findOne(Long id) {
        return departementRepository.findById(id);
    }

    public Departement save(Departement departement) {
        return departementRepository.save(departement);
    }

    public void delete(Long id) {
        departementRepository.deleteById(id);
    }
}
