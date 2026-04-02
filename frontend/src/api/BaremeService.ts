import api from './index'

export interface BaremeDTO {
  id?: number
  code: string
  libelle?: string | null
  actif?: boolean
  parDefaut?: boolean
}

export interface BaremeCritereDTO {
  id?: number
  baremeId: number | null
  baremeCode?: string | null
  critereId: number | null
  critereLibelle?: string | null
  coefficient: number | null
}

export const BaremeService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/baremes', { params: { page, size } })
  },

  getOne (id: number) {
    return api.get(`/api/v1/baremes/${id}`)
  },

  create (bareme: BaremeDTO) {
    return api.post('/api/v1/baremes', bareme)
  },

  update (id: number, bareme: BaremeDTO) {
    return api.put(`/api/v1/baremes/${id}`, bareme)
  },

  delete (id: number) {
    return api.delete(`/api/v1/baremes/${id}`)
  }
}

export const BaremeCritereService = {
  getAll (page = 0, size = 20, baremeId?: number | null) {
    return api.get('/api/v1/bareme-criteres', {
      params: {
        page,
        size,
        ...(baremeId != null ? { baremeId } : {})
      }
    })
  },

  create (dto: BaremeCritereDTO) {
    return api.post('/api/v1/bareme-criteres', dto)
  },

  update (id: number, dto: BaremeCritereDTO) {
    return api.put(`/api/v1/bareme-criteres/${id}`, dto)
  },

  delete (id: number) {
    return api.delete(`/api/v1/bareme-criteres/${id}`)
  }
}
