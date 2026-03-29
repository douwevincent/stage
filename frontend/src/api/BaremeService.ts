import api from './index'

export interface BaremeDTO {
  id?: number
  code: string
  libelle?: string | null
  actif?: boolean
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
    return api.get('/api/baremes', { params: { page, size } })
  },

  getOne (id: number) {
    return api.get(`/api/baremes/${id}`)
  },

  create (bareme: BaremeDTO) {
    return api.post('/api/baremes', bareme)
  },

  update (id: number, bareme: BaremeDTO) {
    return api.put(`/api/baremes/${id}`, bareme)
  },

  delete (id: number) {
    return api.delete(`/api/baremes/${id}`)
  }
}

export const BaremeCritereService = {
  getAll (page = 0, size = 20, baremeId?: number | null) {
    return api.get('/api/bareme-criteres', {
      params: {
        page,
        size,
        ...(baremeId != null ? { baremeId } : {})
      }
    })
  },

  create (dto: BaremeCritereDTO) {
    return api.post('/api/bareme-criteres', dto)
  },

  update (id: number, dto: BaremeCritereDTO) {
    return api.put(`/api/bareme-criteres/${id}`, dto)
  },

  delete (id: number) {
    return api.delete(`/api/bareme-criteres/${id}`)
  }
}
