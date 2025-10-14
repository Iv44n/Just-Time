import apiClient from '@/config/apiClient'
import type { User } from '@/store/slices/authSlice'

export async function loginUser(
  email: string,
  password: string
): Promise<User> {
  return apiClient.post('/auth/login', { email, password })
}

export async function registerUser(
  email: string,
  password: string,
  username: string,
  role: 'ROLE_USER' | 'ROLE_ADMIN'
): Promise<User> {
  return apiClient.post('/auth/register', { email, password, username, role })
}

export async function logoutUser() {
  return apiClient.post('/auth/logout')
}

export async function getCurrentUser(): Promise<User> {
  return apiClient.get('/auth/me', {
    params: { email: 'ivan@test.com' }
  })
}
