import api from './index'

export interface NiveauDTO {
  id?: number
  libelle: string
}

export const NiveauService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/niveaus', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/niveaus/${id}`)
  },

  create (niveau: NiveauDTO) {
    return api.post('/api/niveaus', niveau)
  },

  update (id: number, niveau: NiveauDTO) {
    return api.put(`/api/niveaus/${id}`, niveau)
  },

  delete (id: number) {
    return api.delete(`/api/niveaus/${id}`)
  }
}