import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { AuthService, type UserAccountDTO } from '@/api/AuthService'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('auth_token'))
  const user = ref<UserAccountDTO | null>(null)

  const isAuthenticated = computed(() => Boolean(token.value))
  const isSuperAdmin = computed(() => user.value?.role === 'SUPER_ADMIN')

  function persist() {
    if (token.value) {
      localStorage.setItem('auth_token', token.value)
    } else {
      localStorage.removeItem('auth_token')
    }

    if (user.value) {
      localStorage.setItem('auth_user', JSON.stringify(user.value))
    } else {
      localStorage.removeItem('auth_user')
    }
  }

  function loadUserFromStorage() {
    const raw = localStorage.getItem('auth_user')
    if (!raw) {
      return
    }
    try {
      user.value = JSON.parse(raw)
    } catch {
      user.value = null
    }
  }

  async function initialize() {
    loadUserFromStorage()
    if (!token.value) {
      return
    }
    try {
      const res = await AuthService.me()
      user.value = res.data
      persist()
    } catch {
      logout()
    }
  }

  async function login(email: string, password: string) {
    const res = await AuthService.login({ email, password })
    token.value = res.data.accessToken
    user.value = res.data.user
    persist()
  }

  function logout() {
    token.value = null
    user.value = null
    persist()
  }

  return {
    token,
    user,
    isAuthenticated,
    isSuperAdmin,
    initialize,
    login,
    logout,
  }
})
