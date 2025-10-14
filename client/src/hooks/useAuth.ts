import { useAppSelector } from './useBaseRedux'

export function useAuth() {
  const auth = useAppSelector(state => state.auth)
  return auth
}
