import api from './index'

export interface EncadreurDTO {
  id?: number
  nom: string
  prenom?: string | null
  email: string
  entrepriseId: number
}

export const EncadreurService = {
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
  }
}
