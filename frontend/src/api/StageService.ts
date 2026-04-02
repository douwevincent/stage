import api, { buildAppPath } from './index'
import type { AxiosResponse } from 'axios'

export type Source = 'ETUDIANT' | 'OPERATEUR'
export type Statut = 'EN_ATTENTE_VALIDATION' | 'VALIDE' | 'REJETE'

export interface StageDTO {
  id?: number
  etudiantId?: number | null
  etudiantMatricule?: string | null
  etudiantNom?: string | null
  entrepriseId?: number | null
  entrepriseNom?: string | null
  ville?: string | null
  adresse?: string | null
  encadreurId?: number | null
  dateDebut?: string | null
  dateFin?: string | null
  anneeAcademiqueId?: number | null
  sessionEvaluationId?: number | null
  source?: Source | null
  statut?: Statut | null
  cheminAutorisation?: string | null
}

export const StageService = {
  getAll (page = 0, size = 20, statut?: Statut) {
    return api.get('/api/v1/stages', {
      params: { page, size, ...(statut ? { statut } : {}) }
    })
  },

  getOne (id: number) {
    return api.get<StageDTO>(`/api/v1/stages/${id}`)
  },

  create (stage: StageDTO) {
    return api.post<StageDTO>('/api/v1/stages', stage)
  },

  update (id: number, stage: StageDTO) {
    return api.put<StageDTO>(`/api/v1/stages/${id}`, stage)
  },

  delete (id: number) {
    return api.delete(`/api/v1/stages/${id}`)
  },

  valider (id: number) {
    return api.patch<StageDTO>(`/api/v1/stages/${id}/valider`)
  },

  rejeter (id: number) {
    return api.patch<StageDTO>(`/api/v1/stages/${id}/rejeter`)
  },

  assignerEtudiant (id: number, etudiantId: number) {
    return api.patch<StageDTO>(`/api/v1/stages/${id}/assigner-etudiant`, null, {
      params: { etudiantId }
    })
  },

  getAutorisationUrl (id: number): string {
    return buildAppPath(`/api/v1/stages/${id}/autorisation`)
  },

  declarer (params: {
    etudiantMatricule: string
    entrepriseId?: number | null
    entrepriseNom?: string | null
    entrepriseSecteur?: string | null
    ville: string
    adresse: string
    dateDebut: string
    dateFin: string
    autorisation: File
  }): Promise<AxiosResponse<StageDTO>> {
    const form = new FormData()
    form.append('etudiantMatricule', params.etudiantMatricule)
    if (params.entrepriseId != null) form.append('entrepriseId', String(params.entrepriseId))
    if (params.entrepriseNom) form.append('entrepriseNom', params.entrepriseNom)
    if (params.entrepriseSecteur) form.append('entrepriseSecteur', params.entrepriseSecteur)
    form.append('ville', params.ville)
    form.append('adresse', params.adresse)
    form.append('dateDebut', params.dateDebut)
    form.append('dateFin', params.dateFin)
    form.append('autorisation', params.autorisation)
    return api.post<StageDTO>('/api/v1/stages/declarer', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
