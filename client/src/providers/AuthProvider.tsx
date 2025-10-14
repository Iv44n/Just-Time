'use client'

import { useEffect, useState } from 'react'
import { fetchCurrentUser } from '@/store/slices/authSlice'
import { useAppDispatch } from '@/hooks/useBaseRedux'

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const dispatch = useAppDispatch()
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    dispatch(fetchCurrentUser()).finally(() => setIsReady(true))
  }, [dispatch])

  if (!isReady) return null

  return <>{children}</>
}
