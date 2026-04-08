import api from './index'

export interface DashboardStatsDTO {
  anneeAcademiqueId: number
  anneeAcademiqueLibelle: string
  nombreEtudiantsInscrits: number
  nombreStagesEnregistres: number
  nombreStagesEnAttenteValidation: number
  nombreStagesEnAttenteNotation: number
  nombreStagesSansEtudiant: number
  nombreEntreprisesAvecStages: number
}

export const DashboardService = {
  getStatsAnneeActive () {
    return api.get<DashboardStatsDTO>('/api/v1/dashboard/stats')
  }
}
