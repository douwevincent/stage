import api from './index'

export interface EtudiantDTO {
  id?: number
  matricule: string
  nom: string
  prenom: string
  grade: string
  telephone: string
  email: string
  dateNaissance: string
  lieuNaissance: string
}

export interface StageDeclarationContextDTO {
  etudiant: EtudiantDTO
  typeStageId: number
  typeStageLibelle: string
  dateDebut: string
  dateFin: string
}

export interface EtudiantImportRowDTO {
  no?: number
  matricule: string
  nom: string
  email: string
  telephone: string
  libelleNiveau: string
  codeDepartement: string
  codeSpecialite: string
}

export interface ImportRowMessageDTO {
  no: number
  matricule: string
  message: string
}

export interface EtudiantImportResultDTO {
  totalLignes: number
  etudiantsCrees: number
  etudiantsExistants: number
  inscriptionsCreees: number
  avertissements: number
  erreurs: number
  detailsErreurs: ImportRowMessageDTO[]
  detailsAvertissements: ImportRowMessageDTO[]
}

export const EtudiantService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/v1/etudiants', {
      params: { page, size }
    })
  },

  search (q = '', page = 0, size = 20) {
    return api.get('/api/v1/etudiants/recherche', {
      params: { q, page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/v1/etudiants/${id}`)
  },

  create (etudiant: EtudiantDTO) {
    return api.post('/api/v1/etudiants', etudiant)
  },

  update (id: number, etudiant: EtudiantDTO) {
    return api.put(`/api/v1/etudiants/${id}`, etudiant)
  },

  importRows (rows: EtudiantImportRowDTO[]) {
    return api.post<EtudiantImportResultDTO>('/api/v1/etudiants/import', rows)
  },

  delete (id: number) {
    return api.delete(`/api/v1/etudiants/${id}`)
  },

  validateMatricule (matricule: string) {
    return api.get<EtudiantDTO>(`/api/v1/etudiants/validate-matricule/${encodeURIComponent(matricule)}`)
  },

  getStageDeclarationContext (matricule: string) {
    return api.get<StageDeclarationContextDTO>(`/api/v1/etudiants/validate-matricule/${encodeURIComponent(matricule)}/declaration-context`)
  }
}
