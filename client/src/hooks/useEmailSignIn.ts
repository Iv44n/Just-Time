import { loginUser } from '@/lib/api'
import { useState } from 'react'
import { useAuthStore } from '@/store/useAuthStore'

interface SignInData {
  email: string
  password: string
}

export default function useEmailSignIn() {
  const setUser = useAuthStore(state => state.setUser)
  const [status, setStatus] = useState({
    loading: false,
    errorMessage: null as string | null,
    success: false
  })

  const handleEmailSignIn = async (data: SignInData) => {
    try {
      setStatus({ loading: true, errorMessage: null, success: false })
      const user = await loginUser(data.email, data.password)
      setUser(user)
      setStatus({ loading: false, errorMessage: null, success: true })
    } catch (error: any) {
      setStatus({ loading: false, errorMessage: error.message, success: false })
    }
  }

  return {
    handleEmailSignIn,
    status
  }
}
