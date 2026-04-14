import api from './index'

export const MailQueueStatut = {
  PENDING: 'PENDING',
  SENT: 'SENT',
  FAILED: 'FAILED'
} as const

export type MailQueueStatut = typeof MailQueueStatut[keyof typeof MailQueueStatut]

export interface MailQueueDTO {
  id: number
  destinataireEmail: string
  sujet: string
  corps: string
  statut: MailQueueStatut
  datePlanifiee: string
  dateEnvoi: string | null
  nombreTentatives: number
  erreur: string | null
  encadreurId: number
  stageId: number | null
  periodeStageId: number | null
  notificationId: number
  createdAt: string
  updatedAt: string
}

export interface RetryFailedResponse {
  retried: number
  limit: number
}

export interface CleanupResponse {
  deleted: number
  olderThanDays: number
}

export const MailQueueService = {
  getAll(page = 0, size = 20, statut?: MailQueueStatut) {
    return api.get('/api/v1/admin/mail-queue', {
      params: { page, size, statut }
    })
  },

  retry(id: number) {
    return api.post<MailQueueDTO>(`/api/v1/admin/mail-queue/${id}/retry`)
  },

  retryFailed(limit = 100) {
    return api.post<RetryFailedResponse>('/api/v1/admin/mail-queue/retry-failed', null, {
      params: { limit }
    })
  },

  delete(id: number) {
    return api.delete(`/api/v1/admin/mail-queue/${id}`)
  },

  cleanup(olderThanDays = 30, statut?: MailQueueStatut) {
    return api.delete<CleanupResponse>('/api/v1/admin/mail-queue/cleanup', {
      params: { olderThanDays, statut }
    })
  }
}
