import api from './index'

export interface PeriodeStageDTO {
  id?: number
  typeStageId: number | null
  anneeAcademiqueId: number | null
  dateDebut: string
  dateFin: string
}

export const PeriodeStageService = {
  getAll (page = 0, size = 20, anneeAcademiqueId?: number) {
    return api.get('/api/periode-stages', {
      params: { page, size, anneeAcademiqueId }
    })
  },

  getOne (id: number) {
    return api.get(`/api/periode-stages/${id}`)
  },

  create (periodeStage: PeriodeStageDTO) {
    return api.post('/api/periode-stages', periodeStage)
  },

  update (id: number, periodeStage: PeriodeStageDTO) {
    return api.put(`/api/periode-stages/${id}`, periodeStage)
  },

  delete (id: number) {
    return api.delete(`/api/periode-stages/${id}`)
  }
}
