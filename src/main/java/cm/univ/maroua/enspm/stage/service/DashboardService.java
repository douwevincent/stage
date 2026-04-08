package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.AnneeAcademique;
import cm.univ.maroua.enspm.stage.domain.Statut;
import cm.univ.maroua.enspm.stage.repository.AnneeAcademiqueRepository;
import cm.univ.maroua.enspm.stage.repository.InscriptionRepository;
import cm.univ.maroua.enspm.stage.repository.StageRepository;
import cm.univ.maroua.enspm.stage.service.dto.DashboardStatsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final InscriptionRepository inscriptionRepository;
    private final StageRepository stageRepository;

    public DashboardService(
            AnneeAcademiqueRepository anneeAcademiqueRepository,
            InscriptionRepository inscriptionRepository,
            StageRepository stageRepository
    ) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.stageRepository = stageRepository;
    }

    public DashboardStatsDTO getStatsAnneeActive() {
        AnneeAcademique anneeActive = anneeAcademiqueRepository.findByActifTrue()
                .orElseThrow(() -> new IllegalStateException("Aucune année académique active"));

        Long anneeId = anneeActive.getId();

        return new DashboardStatsDTO(
                anneeId,
                anneeActive.getLibelle(),
                inscriptionRepository.countByAnneeAcademiqueId(anneeId),
                stageRepository.countByAnneeAcademiqueId(anneeId),
                stageRepository.countByAnneeAcademiqueIdAndStatut(anneeId, Statut.EN_ATTENTE_VALIDATION),
                stageRepository.countStagesEnAttenteNotation(anneeId),
                stageRepository.countByAnneeAcademiqueIdAndEtudiantIsNull(anneeId),
                stageRepository.countDistinctEntreprisesByAnneeAcademiqueId(anneeId)
        );
    }
}
