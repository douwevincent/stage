import api from './index'

export interface NiveauDTO {
  id?: number
  libelle: string
  typeStageId?: number | null
  typeStageLibelle?: string | null
}

export const NiveauService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/niveaus', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/niveaus/${id}`)
  },

  create (niveau: NiveauDTO) {
    return api.post('/api/v1/niveaus', niveau)
  },

  update (id: number, niveau: NiveauDTO) {
    return api.put(`/api/v1/niveaus/${id}`, niveau)
  },

  delete (id: number) {
    return api.delete(`/api/v1/niveaus/${id}`)
  }
}