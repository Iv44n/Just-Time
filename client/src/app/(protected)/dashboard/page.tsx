'use client'
import { useAuth } from '@/hooks/useAuth'

export default function DashboardPage() {
  const { user } = useAuth()

  return (
    <div>
      <h1>{JSON.stringify(user, null, 2)}</h1>
      <h1>Holaaa</h1>
    </div>
  )
}
