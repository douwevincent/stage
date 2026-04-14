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
  },

  async downloadExportByNiveau (niveauId: number, format: 'pdf' | 'excel'): Promise<void> {
    await downloadExportFile(
      `/api/v1/evaluation-exports/by-niveau/${niveauId}/${format}`,
      `resultats-niveau-${niveauId}.${format === 'pdf' ? 'pdf' : 'xlsx'}`,
      format
    )
  },

  async downloadExportByParcours (parcoursId: number, format: 'pdf' | 'excel'): Promise<void> {
    await downloadExportFile(
      `/api/v1/evaluation-exports/by-parcours/${parcoursId}/${format}`,
      `resultats-parcours-${parcoursId}.${format === 'pdf' ? 'pdf' : 'xlsx'}`,
      format
    )
  },

  async downloadExportByTypeStage (typeStageId: number, format: 'pdf' | 'excel'): Promise<void> {
    await downloadExportFile(
      `/api/v1/evaluation-exports/by-type-stage/${typeStageId}/${format}`,
      `resultats-type-stage-${typeStageId}.${format === 'pdf' ? 'pdf' : 'xlsx'}`,
      format
    )
  }
}

async function downloadExportFile (urlPath: string, fallbackFileName: string, format: 'pdf' | 'excel'): Promise<void> {
  const response = await api.get(urlPath, {
    responseType: 'blob'
  })

  const contentType = format === 'pdf'
    ? 'application/pdf'
    : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const blob = new Blob([response.data], { type: contentType })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = extractFilename(response.headers?.['content-disposition']) ?? fallbackFileName
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}

function extractFilename (contentDisposition?: string): string | null {
  if (!contentDisposition) return null
  const match = contentDisposition.match(/filename="?([^";]+)"?/i)
  return match?.[1] ?? null
}
