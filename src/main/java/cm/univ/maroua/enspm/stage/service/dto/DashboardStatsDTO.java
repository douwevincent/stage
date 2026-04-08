package cm.univ.maroua.enspm.stage.service.dto;

import java.io.Serializable;

public record DashboardStatsDTO(
        Long anneeAcademiqueId,
        String anneeAcademiqueLibelle,
        long nombreEtudiantsInscrits,
        long nombreStagesEnregistres,
        long nombreStagesEnAttenteValidation,
        long nombreStagesEnAttenteNotation,
        long nombreStagesSansEtudiant,
        long nombreEntreprisesAvecStages
) implements Serializable {
}
