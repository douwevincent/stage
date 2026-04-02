import api from './index'

export interface TypeStageDTO {
  id?: number
  libelle: string
}

export const TypeStageService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/type-stages', {
      params: { page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/type-stages/${id}`)
  },

  create (typeStage: TypeStageDTO) {
    return api.post('/api/v1/type-stages', typeStage)
  },

  update (id: number, typeStage: TypeStageDTO) {
    return api.put(`/api/v1/type-stages/${id}`, typeStage)
  },

  delete (id: number) {
    return api.delete(`/api/v1/type-stages/${id}`)
  }
}