import api from './index'

export interface EncadreurDTO {
  id?: number
  nom: string
  prenom?: string | null
  email: string
  entrepriseId: number
}

export const EncadreurService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/encadreurs', {
      params: { page, size }
    })
  },

  getById (id: number) {
    return api.get(`/api/v1/encadreurs/${id}`)
  },

  search (params: { entrepriseId?: number | null, q?: string, page?: number, size?: number }) {
    return api.get('/api/v1/encadreurs/recherche', {
      params: {
        entrepriseId: params.entrepriseId ?? undefined,
        q: params.q ?? '',
        page: params.page ?? 0,
        size: params.size ?? 20,
      }
    })
  },

  create (encadreur: EncadreurDTO) {
    return api.post<EncadreurDTO>('/api/v1/encadreurs', encadreur)
  },

  update (id: number, encadreur: EncadreurDTO) {
    return api.put<EncadreurDTO>(`/api/v1/encadreurs/${id}`, encadreur)
  },

  delete (id: number) {
    return api.delete(`/api/v1/encadreurs/${id}`)
  }
}
