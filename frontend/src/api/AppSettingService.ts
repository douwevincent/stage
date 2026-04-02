import api from './index'

export interface AppSettingDTO {
  id: number
  cle: string
  valeur: string
  type: 'STRING' | 'INTEGER' | 'TEXT'
  description: string
  secret: boolean
  modifiable: boolean
  updatedAt: string
}

export const AppSettingService = {
  getAll () {
    return api.get('/api/v1/parametres')
  },

  update (cle: string, valeur: string) {
    return api.put(`/api/v1/parametres/${cle}`, { valeur })
  }
}