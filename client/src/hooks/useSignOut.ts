import { logoutUser } from '@/lib/api'
import { useState } from 'react'
import { useAuthStore } from '@/store/useAuthStore'

export const useSignOut = () => {
  const setUser = useAuthStore(state => state.setUser)
  const [status, setStatus] = useState({
    loading: false,
    errorMessage: null as string | null,
    success: false
  })

  const logout = async () => {
    try {
      setStatus({ loading: true, errorMessage: null, success: false })
      await logoutUser()
      setUser(null)
      setStatus({ loading: false, errorMessage: null, success: true })
    } catch (error: any) {
      setStatus({ loading: false, errorMessage: error.message, success: false })
    }
  }

  return {
    logout,
    status
  }
}
