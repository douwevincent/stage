import api from './index'

export interface SpecialiteDTO {
  id?: number
  code: string
  intitule: string
  departementId: number | null
}

export const SpecialiteService = {
  getAll (page = 0, size = 20, departementId?: number | null) {
    return api.get('/api/specialites', {
      params: { page, size, departementId }
    })
  },

  getOne (id: number) {
    return api.get(`/api/specialites/${id}`)
  },

  create (specialite: SpecialiteDTO) {
    return api.post('/api/specialites', specialite)
  },

  update (id: number, specialite: SpecialiteDTO) {
    return api.put(`/api/specialites/${id}`, specialite)
  },

  delete (id: number) {
    return api.delete(`/api/specialites/${id}`)
  }
}
