package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    public AnneeAcademiqueService(AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    public Page<AnneeAcademique> findAll(Pageable pageable) {
        return anneeAcademiqueRepository.findAll(pageable);
    }

    public Optional<AnneeAcademique> findOne(Long id) {
        return anneeAcademiqueRepository.findById(id);
    }

    public AnneeAcademique save(AnneeAcademique anneeAcademique) {
        return anneeAcademiqueRepository.save(anneeAcademique);
    }

    public void delete(Long id) {
        anneeAcademiqueRepository.deleteById(id);
    }
}
