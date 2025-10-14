import ProtectedRoute from '@/components/ProtectedRoute'

export default function ProtectedLayout({
  children
}: Readonly<{
  children: React.ReactNode
}>) {
  return <ProtectedRoute>{children}</ProtectedRoute>
}
