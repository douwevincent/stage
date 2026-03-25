import api from './index'

export interface EntrepriseDTO {
  id?: number
  nom: string
  secteur: string
}

export const EntrepriseService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/entreprises', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/entreprises/${id}`)
  },

  create (entreprise: EntrepriseDTO) {
    return api.post('/api/entreprises', entreprise)
  },

  update (id: number, entreprise: EntrepriseDTO) {
    return api.put(`/api/entreprises/${id}`, entreprise)
  },

  delete (id: number) {
    return api.delete(`/api/entreprises/${id}`)
  }
}
