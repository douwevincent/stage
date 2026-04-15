import api from './index'

export interface ParcoursDTO {
  id?: number
  departementId?: number | null
  departementCode?: string | null
  departementIntitule?: string | null
  specialiteId: number | null
  niveauId: number | null
  baremeId?: number | null
  baremeCode?: string | null
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
      baremeId?: number | null
      q?: string
      sort?: string
    }
  ) {
    const params: Record<string, string | number> = { page, size }
    if (filters?.departementId != null) params.departementId = filters.departementId
    if (filters?.specialiteId != null) params.specialiteId = filters.specialiteId
    if (filters?.niveauId != null) params.niveauId = filters.niveauId
    if (filters?.baremeId != null) params.baremeId = filters.baremeId
    if (filters?.q) params.q = filters.q
    if (filters?.sort) params.sort = filters.sort

    return api.get('/api/v1/parcours', {
      params
    })
  },

  async getCatalog () {
    const firstPageSize = 1000
    const response = await this.getAll(0, firstPageSize)
    const content = response.data.content || []
    const totalElements = response.data.totalElements || response.data.page?.totalElements || content.length

    if (content.length >= totalElements) {
      return content
    }

    const fullResponse = await this.getAll(0, totalElements)
    return fullResponse.data.content || []
  },

  getOne (id: number) {
    return api.get(`/api/v1/parcours/${id}`)
  },

  create (parcours: ParcoursDTO) {
    return api.post('/api/v1/parcours', parcours)
  },

  update (id: number, parcours: ParcoursDTO) {
    return api.put(`/api/v1/parcours/${id}`, parcours)
  },

  delete (id: number) {
    return api.delete(`/api/v1/parcours/${id}`)
  }
}