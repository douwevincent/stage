import api from './index'

export interface SpecialiteDTO {
  id?: number
  code: string
  intitule: string
  departementId: number | null
}

export const SpecialiteService = {
  getAll (page = 0, size = 20, departementId?: number | null) {
    return api.get('/api/v1/specialites', {
      params: { page, size, departementId }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/specialites/${id}`)
  },

  create (specialite: SpecialiteDTO) {
    return api.post('/api/v1/specialites', specialite)
  },

  update (id: number, specialite: SpecialiteDTO) {
    return api.put(`/api/v1/specialites/${id}`, specialite)
  },

  delete (id: number) {
    return api.delete(`/api/v1/specialites/${id}`)
  }
}
