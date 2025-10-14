import axios, { type AxiosRequestConfig } from 'axios'
import { BACKEND_URL } from '@/config/env'

const baseConfig: AxiosRequestConfig = {
  baseURL: `${BACKEND_URL}/api`,
  withCredentials: true
}

const TokenRefreshClient = axios.create(baseConfig)
TokenRefreshClient.interceptors.response.use(response => response.data)

const API = axios.create(baseConfig)

API.interceptors.response.use(
  response => response.data,
  async error => {
    const { config, response } = error
    const { status, data } = response || {}

    if (status === 401 && data?.errorCode === 'InvalidAccessToken') {
      try {
        await TokenRefreshClient.get('/auth/refresh')
        return TokenRefreshClient(config)
      } catch {
        console.warn('Error al refrescar el token de acceso')
      }
    }

    return Promise.reject({ status, ...data })
  }
)

export default API
