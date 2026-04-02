import axios from 'axios'

const rawBaseUrl = import.meta.env.BASE_URL || '/'
const normalizedBaseUrl = rawBaseUrl.endsWith('/') ? rawBaseUrl : `${rawBaseUrl}/`
const appBasePath = normalizedBaseUrl === '/' ? '' : normalizedBaseUrl.slice(0, -1)

export const buildAppPath = (path: string): string => {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  return appBasePath ? `${appBasePath}${normalizedPath}` : normalizedPath
}

const api = axios.create({
  baseURL: normalizedBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_user')
      const loginPath = buildAppPath('/login')
      if (window.location.pathname !== loginPath) {
        window.location.href = loginPath
      }
    }
    // Handle global errors here
    console.error('API Error:', error.response?.data || error.message)
    return Promise.reject(error)
  }
)

export default api
