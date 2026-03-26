package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.domain.Inscription;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.service.dto.InscriptionDTO;
import cm.univ.maroua.enspm.stage.service.mapper.InscriptionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final InscriptionMapper inscriptionMapper;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository, InscriptionMapper inscriptionMapper,
            AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.inscriptionMapper = inscriptionMapper;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    public Page<InscriptionDTO> findAll(Pageable pageable) {
        return inscriptionRepository.findAll(pageable).map(inscriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InscriptionDTO> findAll(Pageable pageable, Long anneeAcademiqueId, Long etudiantId, Long parcoursId,
            String q) {
        Specification<Inscription> spec = (root, query, cb) -> cb.conjunction();

        if (anneeAcademiqueId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("anneeAcademique").get("id"), anneeAcademiqueId));
        }

        if (etudiantId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("etudiant").get("id"), etudiantId));
        }

        if (parcoursId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("parcours").get("id"), parcoursId));
        }

        if (q != null && !q.isBlank()) {
            String search = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("anneeAcademique").get("libelle")), search),
                    cb.like(cb.lower(root.get("etudiant").get("matricule")), search),
                    cb.like(cb.lower(root.get("etudiant").get("nom")), search),
                    cb.like(cb.lower(root.get("parcours").get("specialite").get("code")), search),
                    cb.like(cb.lower(root.get("parcours").get("specialite").get("intitule")), search),
                    cb.like(cb.lower(root.get("parcours").get("niveau").get("libelle")), search)));
        }

        return inscriptionRepository.findAll(spec, pageable).map(inscriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<InscriptionDTO> findOne(Long id) {
        return inscriptionRepository.findById(id).map(inscriptionMapper::toDto);
    }

    public InscriptionDTO save(InscriptionDTO inscriptionDTO) {
        Inscription inscription = inscriptionMapper.toEntity(inscriptionDTO);
        if (inscription.getAnneeAcademique() == null || inscription.getAnneeAcademique().getId() == null) {
            AnneeAcademique active = anneeAcademiqueRepository.findByActifTrue()
                    .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));
            inscription.setAnneeAcademique(active);
        }
        inscription = inscriptionRepository.save(inscription);
        return inscriptionMapper.toDto(inscription);
    }

    public void delete(Long id) {
        inscriptionRepository.deleteById(id);
    }
}
