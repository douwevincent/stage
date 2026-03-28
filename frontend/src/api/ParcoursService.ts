import api from './index'

export interface ParcoursDTO {
  id?: number
  departementId?: number | null
  departementCode?: string | null
  departementIntitule?: string | null
  specialiteId: number | null
  niveauId: number | null
  specialiteCode?: string | null
  specialiteIntitule?: string | null
  niveauLibelle?: string | null
  libelle?: string | null
}

export const ParcoursService = {
  getAll (
    page = 0,
    size = 20,
    filters?: {
      departementId?: number | null
      specialiteId?: number | null
      niveauId?: number | null
      q?: string
      sort?: string
    }
  ) {
    const params: Record<string, string | number> = { page, size }
    if (filters?.departementId != null) params.departementId = filters.departementId
    if (filters?.specialiteId != null) params.specialiteId = filters.specialiteId
    if (filters?.niveauId != null) params.niveauId = filters.niveauId
    if (filters?.q) params.q = filters.q
    if (filters?.sort) params.sort = filters.sort

    return api.get('/api/parcours', {
      params
    })
  },

  getOne (id: number) {
    return api.get(`/api/parcours/${id}`)
  },

  create (parcours: ParcoursDTO) {
    return api.post('/api/parcours', parcours)
  },

  update (id: number, parcours: ParcoursDTO) {
    return api.put(`/api/parcours/${id}`, parcours)
  },

  delete (id: number) {
    return api.delete(`/api/parcours/${id}`)
  }
}