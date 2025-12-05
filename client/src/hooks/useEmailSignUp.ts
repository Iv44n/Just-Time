import { useState } from 'react'
import { registerUser } from '@/lib/api'
import { useAuthStore } from '@/store/useAuthStore'

interface RegisterData {
  username: string
  email: string
  password: string
  role: 'ROLE_USER' | 'ROLE_ADMIN'
}

export default function useEmailSignUp() {
  const setUser = useAuthStore(state => state.setUser)
  const [status, setStatus] = useState({
    loading: false,
    errorMessage: null as string | null,
    success: false
  })

  const handleEmailSignUp = async ({
    email,
    password,
    username,
    role
  }: RegisterData) => {
    try {
      setStatus({ loading: true, errorMessage: null, success: false })
      const user = await registerUser(email, password, username, role)
      setUser(user)
      setStatus({ loading: false, errorMessage: null, success: true })
    } catch (error: any) {
      setStatus({ loading: false, errorMessage: error.message, success: false })
    }
  }

  return {
    handleEmailSignUp,
    status
  }
}
