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

export const EtudiantService = {
  getAll (page = 0, size = 20) {
    return api.get('/api/etudiants', {
      params: { page, size }
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

  delete (id: number) {
    return api.delete(`/api/etudiants/${id}`)
  }
}
