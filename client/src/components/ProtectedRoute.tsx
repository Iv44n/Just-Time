'use client'

import { useAuth } from '@/hooks/useAuth'
import { useRouter, usePathname } from 'next/navigation'
import { useEffect, useState } from 'react'

interface ProtectedRouteProps {
  children: React.ReactNode
}

export default function ProtectedRoute({ children }: ProtectedRouteProps) {
  const router = useRouter()
  const pathname = usePathname()

  const { user, isAuthenticated } = useAuth()

  const [allowed, setAllowed] = useState<boolean | null>(null)

  useEffect(() => {
    // No autenticado → fuera
    if (!isAuthenticated) {
      router.replace('/')
      setAllowed(false)
      return
    }

    const userRoles = Array.isArray(user?.roles)
      ? [...user.roles]
      : user?.roles
        ? [user.roles]
        : []

    const isAdmin = userRoles.includes('ROLE_ADMIN')
    const isUser = userRoles.includes('ROLE_USER')

    if (isAdmin && !pathname.startsWith('/admin')) {
      router.replace('/admin')
      setAllowed(false)
      return
    }

    if (isUser && !pathname.startsWith('/dashboard')) {
      router.replace('/dashboard')
      setAllowed(false)
      return
    }

    setAllowed(true)
  }, [isAuthenticated, router, pathname, user])

  // Pantalla de carga mientras decide
  if (allowed === null) {
    return (
      <div className='flex items-center justify-center h-screen'>
        <div className='animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600'></div>
      </div>
    )
  }

  if (allowed === false) return null

  return <>{children}</>
}
