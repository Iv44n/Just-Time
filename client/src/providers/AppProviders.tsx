'use client'

import { AuthProvider } from '@/providers/AuthProvider'
import { QueryClientProvider } from '@tanstack/react-query'
import queryClient from '@/config/queryClient'

export default function AppProviders({
  children
}: {
  children: React.ReactNode
}) {
  return (
    <AuthProvider>
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </AuthProvider>
  )
}
