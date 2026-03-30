import api from './index'

export interface CritereDTO {
  id?: number
  libelle: string
  categorie?: string
}

export const CritereService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/criteres', { params: { page, size } })
  },

  getOne (id: number) {
    return api.get(`/api/criteres/${id}`)
  },

  create (critere: CritereDTO) {
    return api.post('/api/criteres', critere)
  },

  update (id: number, critere: CritereDTO) {
    return api.put(`/api/criteres/${id}`, critere)
  },

  delete (id: number) {
    return api.delete(`/api/criteres/${id}`)
  }
}
