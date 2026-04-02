import api from './index'
import type { UserAccountDTO } from './AuthService'

export interface UserCreateRequestDTO {
  email: string
  password: string
  role: 'OPERATEUR' | 'SCOLARITE' | 'ADMIN' | 'SUPER_ADMIN'
  active?: boolean
}

export interface UserResetPasswordRequestDTO {
  newPassword: string
}

export interface PagedResponse<T> {
  content: T[]
  totalElements: number
}

export const UserAccountService = {
  getAll(page = 0, size = 10) {
    return api.get<PagedResponse<UserAccountDTO>>('/api/users', {
      params: { page, size }
    })
  },

  create(payload: UserCreateRequestDTO) {
    return api.post<UserAccountDTO>('/api/users', payload)
  },

  activate(id: number) {
    return api.patch<UserAccountDTO>(`/api/users/${id}/activate`)
  },

  deactivate(id: number) {
    return api.patch<UserAccountDTO>(`/api/users/${id}/deactivate`)
  },

  resetPassword(id: number, payload: UserResetPasswordRequestDTO) {
    return api.patch<UserAccountDTO>(`/api/users/${id}/reset-password`, payload)
  },

  delete(id: number) {
    return api.delete(`/api/users/${id}`)
  }
}
