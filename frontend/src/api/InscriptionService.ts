import api from './index'

export interface InscriptionDTO {
  id?: number
  anneeAcademiqueId: number | null
  etudiantId: number | null
  parcoursId: number | null
  anneeAcademiqueLibelle?: string | null
  etudiantMatricule?: string | null
  etudiantNom?: string | null
  parcoursSpecialiteId?: number | null
  parcoursSpecialiteCode?: string | null
  parcoursSpecialiteIntitule?: string | null
  parcoursNiveauId?: number | null
  parcoursNiveauLibelle?: string | null
  parcoursLibelle?: string | null
}

export const InscriptionService = {
  getAll (
    page = 0,
    size = 20,
    filters?: {
      anneeAcademiqueId?: number | null
      etudiantId?: number | null
      parcoursId?: number | null
      q?: string
      sort?: string
    }
  ) {
    const params: Record<string, string | number> = { page, size }
    if (filters?.anneeAcademiqueId != null) params.anneeAcademiqueId = filters.anneeAcademiqueId
    if (filters?.etudiantId != null) params.etudiantId = filters.etudiantId
    if (filters?.parcoursId != null) params.parcoursId = filters.parcoursId
    if (filters?.q) params.q = filters.q
    if (filters?.sort) params.sort = filters.sort

    return api.get('/api/v1/inscriptions', {
      params
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/inscriptions/${id}`)
  },

  create (inscription: InscriptionDTO) {
    return api.post('/api/v1/inscriptions', inscription)
  },

  update (id: number, inscription: InscriptionDTO) {
    return api.put(`/api/v1/inscriptions/${id}`, inscription)
  },

  delete (id: number) {
    return api.delete(`/api/v1/inscriptions/${id}`)
  }
}