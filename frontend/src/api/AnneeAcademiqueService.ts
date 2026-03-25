import api from './index'

export interface AnneeAcademiqueDTO {
  id?: number
  libelle: string
}

export const AnneeAcademiqueService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/annee-academiques', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/annee-academiques/${id}`)
  },

  create (anneeAcademique: AnneeAcademiqueDTO) {
    return api.post('/api/annee-academiques', anneeAcademique)
  },

  update (id: number, anneeAcademique: AnneeAcademiqueDTO) {
    return api.put(`/api/annee-academiques/${id}`, anneeAcademique)
  },

  delete (id: number) {
    return api.delete(`/api/annee-academiques/${id}`)
  }
}