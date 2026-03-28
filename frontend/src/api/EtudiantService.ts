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
    return api.get('/api/etudiants', {
      params: { page, size }
    })
  },

  search (q = '', page = 0, size = 20) {
    return api.get('/api/etudiants/recherche', {
      params: { q, page, size }
    })
  },

  getOne (id: number) {
    return api.get(`/api/etudiants/${id}`)
  },

  create (etudiant: EtudiantDTO) {
    return api.post('/api/etudiants', etudiant)
  },

  update (id: number, etudiant: EtudiantDTO) {
    return api.put(`/api/etudiants/${id}`, etudiant)
  },

  importRows (rows: EtudiantImportRowDTO[]) {
    return api.post<EtudiantImportResultDTO>('/api/etudiants/import', rows)
  },

  delete (id: number) {
    return api.delete(`/api/etudiants/${id}`)
  },

  validateMatricule (matricule: string) {
    return api.get<EtudiantDTO>(`/api/etudiants/validate-matricule/${encodeURIComponent(matricule)}`)
  }
}
