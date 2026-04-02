import api from './index'

export interface AnneeAcademiqueDTO {
  id?: number
  libelle: string
  actif?: boolean
}

export const AnneeAcademiqueService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/annee-academiques', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/annee-academiques/${id}`)
  },

  getActive () {
    return api.get<AnneeAcademiqueDTO>('/api/v1/annee-academiques/active')
  },

  create (anneeAcademique: AnneeAcademiqueDTO) {
    return api.post('/api/v1/annee-academiques', anneeAcademique)
  },

  update (id: number, anneeAcademique: AnneeAcademiqueDTO) {
    return api.put(`/api/v1/annee-academiques/${id}`, anneeAcademique)
  },

  delete (id: number) {
    return api.delete(`/api/v1/annee-academiques/${id}`)
  },

  activate (id: number) {
    return api.patch(`/api/v1/annee-academiques/${id}/activer`)
  }
}