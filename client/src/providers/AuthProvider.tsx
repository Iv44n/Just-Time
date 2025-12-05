'use client'

import { getCurrentUser } from '@/lib/api'
import { useAuthStore } from '@/store/useAuthStore'
import { useEffect, useState } from 'react'

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isReady, setIsReady] = useState(false)
  const setUser = useAuthStore(state => state.setUser)

  useEffect(() => {
    getCurrentUser()
      .then(user => {
        setUser(user)
      })
      .finally(() => {
        setIsReady(true)
      })
  }, [setUser])

  if (!isReady) return null

  return <>{children}</>
}
