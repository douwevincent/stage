package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.service.dto.AnneeAcademiqueDTO;
import cm.univ.maroua.enspm.stage.service.mapper.AnneeAcademiqueMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service metier de gestion des annees academiques.
 *
 * <p>Garantit notamment l'unicite fonctionnelle de l'annee active via
 * l'operation d'activation qui desactive prealablement toutes les autres annees.</p>
 */
@Service
@Transactional
public class AnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final AnneeAcademiqueMapper anneeAcademiqueMapper;

    public AnneeAcademiqueService(AnneeAcademiqueRepository anneeAcademiqueRepository,
            AnneeAcademiqueMapper anneeAcademiqueMapper) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.anneeAcademiqueMapper = anneeAcademiqueMapper;
    }

    /**
     * Retourne la liste paginee des annees academiques.
     */
    public Page<AnneeAcademiqueDTO> findAll(Pageable pageable) {
        return anneeAcademiqueRepository.findAll(pageable).map(anneeAcademiqueMapper::toDto);
    }

    /**
     * Recherche une annee academique par identifiant.
     */
    @Transactional(readOnly = true)
    public Optional<AnneeAcademiqueDTO> findOne(Long id) {
        return anneeAcademiqueRepository.findById(id).map(anneeAcademiqueMapper::toDto);
    }

    /**
     * Retourne l'annee academique actuellement active, si elle existe.
     */
    @Transactional(readOnly = true)
    public Optional<AnneeAcademiqueDTO> findActive() {
        return anneeAcademiqueRepository.findByActifTrue().map(anneeAcademiqueMapper::toDto);
    }

    /**
     * Cree ou met a jour une annee academique.
     */
    public AnneeAcademiqueDTO save(AnneeAcademiqueDTO anneeAcademiqueDTO) {
        AnneeAcademique anneeAcademique = anneeAcademiqueMapper.toEntity(anneeAcademiqueDTO);
        anneeAcademique = anneeAcademiqueRepository.save(anneeAcademique);
        return anneeAcademiqueMapper.toDto(anneeAcademique);
    }

    /**
     * Active une annee academique en desactivant toutes les autres.
     *
     * @param id identifiant de l'annee a activer
     * @return annee academique activee
     */
    public AnneeAcademiqueDTO activate(Long id) {
        anneeAcademiqueRepository.deactivateAll();
        AnneeAcademique annee = anneeAcademiqueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Année académique introuvable : " + id));
        annee.setActif(true);
        return anneeAcademiqueMapper.toDto(anneeAcademiqueRepository.save(annee));
    }

    /**
     * Supprime une annee academique par identifiant.
     */
    public void delete(Long id) {
        anneeAcademiqueRepository.deleteById(id);
    }
}
