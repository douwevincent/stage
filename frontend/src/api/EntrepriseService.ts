import api from './index'

export interface EntrepriseDTO {
  id?: number
  nom: string
  secteur: string
}

export const EntrepriseService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/entreprises', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/entreprises/${id}`)
  },

  create (entreprise: EntrepriseDTO) {
    return api.post('/api/v1/entreprises', entreprise)
  },

  update (id: number, entreprise: EntrepriseDTO) {
    return api.put(`/api/v1/entreprises/${id}`, entreprise)
  },

  delete (id: number) {
    return api.delete(`/api/v1/entreprises/${id}`)
  },

  rechercheParNom (q: string) {
    return api.get<EntrepriseDTO[]>('/api/v1/entreprises/recherche', { params: { q } })
  },

  rechercheOuCree (nom: string, secteur: string) {
    return api.post<EntrepriseDTO>('/api/v1/entreprises/recherche-ou-cree', { nom, secteur })
  }
}
