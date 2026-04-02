import api from './index'

export interface DepartementDTO {
  id?: number
  code: string
  intitule: string
}

export const DepartementService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/departements', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/departements/${id}`)
  },

  create (departement: DepartementDTO) {
    return api.post('/api/v1/departements', departement)
  },

  update (id: number, departement: DepartementDTO) {
    return api.put(`/api/v1/departements/${id}`, departement)
  },

  delete (id: number) {
    return api.delete(`/api/v1/departements/${id}`)
  }
}
