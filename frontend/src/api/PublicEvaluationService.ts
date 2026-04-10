import api from './index'

export interface PublicEvaluationStageItemDTO {
  stageId: number
  sessionId: number
  statut: 'EN_ATTENTE' | 'EN_COURS' | 'TERMINEE'
  etudiantNom: string | null
  matricule: string | null
  entrepriseNom: string | null
  dateDebut: string | null
  dateFin: string | null
  dateLimite: string | null
  totalScore: number
  maxScore: number
}

export interface PublicEvaluationCriterionDTO {
  critereId: number
  libelle: string
  categorie: string
  coefficient: number
}

export interface PublicEvaluationCategoryDTO {
  categorie: string
  criteres: PublicEvaluationCriterionDTO[]
}

export interface PublicEvaluationFormDTO {
  stageId: number
  sessionId: number
  etudiantNom: string | null
  matricule: string | null
  entrepriseNom: string | null
  dateDebut: string | null
  dateFin: string | null
  categories: PublicEvaluationCategoryDTO[]
}

export interface PublicEvaluationNoteInputDTO {
  critereId: number
  valeur: number
  commentaire?: string | null
}

export interface PublicEvaluationSubmitResponse {
  sessionId: number
  statut: 'EN_ATTENTE' | 'EN_COURS' | 'TERMINEE'
  message: string
}

export const PublicEvaluationService = {
  getStages (code: string) {
    return api.get<PublicEvaluationStageItemDTO[]>(`/api/v1/public/evaluations/${encodeURIComponent(code)}/stages`)
  },

  getForm (code: string, stageId: number) {
    return api.get<PublicEvaluationFormDTO>(`/api/v1/public/evaluations/${encodeURIComponent(code)}/stages/${stageId}/form`)
  },

  submit (code: string, stageId: number, notes: PublicEvaluationNoteInputDTO[]) {
    return api.post<PublicEvaluationSubmitResponse>(`/api/v1/public/evaluations/${encodeURIComponent(code)}/submit`, {
      stageId,
      notes
    })
  }
}
