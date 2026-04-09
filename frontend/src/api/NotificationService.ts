import api from './index'

export const NotificationReferenceDateType = {
  DEBUT_STAGE: 'DEBUT_STAGE',
  FIN_STAGE: 'FIN_STAGE',
  DEBUT_PERIODE: 'DEBUT_PERIODE',
  FIN_PERIODE: 'FIN_PERIODE',
  JOURS_AVANT_FIN_STAGE: 'JOURS_AVANT_FIN_STAGE',
  JOURS_APRES_FIN_STAGE: 'JOURS_APRES_FIN_STAGE'
} as const

export type NotificationReferenceDateType = typeof NotificationReferenceDateType[keyof typeof NotificationReferenceDateType]

export interface NotificationDTO {
  id?: number
  typeStageId: number | null
  referenceDateType: NotificationReferenceDateType | null
  offsetDays: number | null
  actif: boolean
}

export const NotificationService = {
  getAll (page = 0, size = 20, typeStageId?: number) {
    return api.get('/api/v1/notifications', {
      params: { page, size, typeStageId }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/notifications/${id}`)
  },

  create (notification: NotificationDTO) {
    return api.post('/api/v1/notifications', notification)
  },

  update (id: number, notification: NotificationDTO) {
    return api.put(`/api/v1/notifications/${id}`, notification)
  },

  delete (id: number) {
    return api.delete(`/api/v1/notifications/${id}`)
  }
}
