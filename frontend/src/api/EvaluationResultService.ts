import api from './index'

export type SessionEvaluationStatut = 'EN_ATTENTE' | 'EN_COURS' | 'TERMINEE'

export interface EvaluationResultSummaryDTO {
  stageId: number | null
  sessionId: number
  statut: SessionEvaluationStatut
  etudiantNom: string | null
  matricule: string | null
  departement: string | null
  niveau: string | null
  specialite: string | null
  entrepriseNom: string | null
  dateDebut: string | null
  dateFin: string | null
  totalScore: number
  maxScore: number
}

export interface EvaluationCriterionDetailDTO {
  critereId: number | null
  critere: string | null
  coefficient: number | null
  note: number | null
  commentaire: string | null
}

export interface EvaluationCategoryDetailDTO {
  categorie: string
  criteres: EvaluationCriterionDetailDTO[]
}

export interface EvaluationResultDetailDTO {
  stageId: number | null
  sessionId: number
  statut: SessionEvaluationStatut
  etudiantNom: string | null
  matricule: string | null
  email: string | null
  telephone: string | null
  anneeAcademique: string | null
  departement: string | null
  niveau: string | null
  specialite: string | null
  entrepriseNom: string | null
  encadreurNom: string | null
  dateDebut: string | null
  dateFin: string | null
  totalScore: number
  maxScore: number
  categories: EvaluationCategoryDetailDTO[]
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
}

export const EvaluationResultService = {
  getAll (params: {
    page?: number
    size?: number
    niveauId?: number | null
    departementId?: number | null
    specialiteId?: number | null
    q?: string
  } = {}) {
    const { page = 0, size = 20, niveauId, departementId, specialiteId, q } = params
    return api.get<PageResponse<EvaluationResultSummaryDTO>>('/api/v1/session-evaluations/results', {
      params: { page, size, niveauId, departementId, specialiteId, q }
    })
  },

  getDetails (sessionId: number) {
    return api.get<EvaluationResultDetailDTO>(`/api/v1/session-evaluations/${sessionId}/details`)
  },

  async downloadSheet (sessionId: number): Promise<void> {
    const response = await api.get(`/api/v1/session-evaluations/${sessionId}/fiche`, {
      responseType: 'blob'
    })

    const blob = new Blob([response.data], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `fiche-evaluation-${sessionId}.pdf`
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  }
}
