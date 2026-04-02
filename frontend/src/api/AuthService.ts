import api from './index'

export interface UserAccountDTO {
  id: number
  email: string
  role: 'OPERATEUR' | 'SCOLARITE' | 'ADMIN' | 'SUPER_ADMIN'
  active: boolean
  createdAt?: string
  updatedAt?: string
}

export interface AuthLoginResponseDTO {
  tokenType: string
  accessToken: string
  user: UserAccountDTO
}

export interface AuthLoginRequestDTO {
  email: string
  password: string
}

export const AuthService = {
  login(payload: AuthLoginRequestDTO) {
    return api.post<AuthLoginResponseDTO>('/api/v1/auth/login', payload)
  },

  me() {
    return api.get<UserAccountDTO>('/api/v1/auth/me')
  },
}
