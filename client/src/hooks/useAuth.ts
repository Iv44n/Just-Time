import { useAuthStore } from '@/store/useAuthStore'

export function useAuth() {
  const auth = useAuthStore(state => state)
  return auth
}
