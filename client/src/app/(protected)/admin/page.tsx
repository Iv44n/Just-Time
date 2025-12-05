'use client'
import DatabaseForm from '@/components/admin/database-tap/DatabaseForm'
import DatabaseList from '@/components/admin/database-tap/DatabaseList'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  useAccessRequests,
  useApproveAccessRequest,
  useRejectAccessRequest
} from '@/hooks/accessRequests'
import { useAuth } from '@/hooks/useAuth'
import { useSignOut } from '@/hooks/useSignOut'
import { LogOut, Shield } from 'lucide-react'

export default function AdminDashboard() {
  const { user } = useAuth()
  const { logout } = useSignOut()
  const { accessRequests } = useAccessRequests()
  const approveAccessRequest = useApproveAccessRequest()
  const rejectAccessRequest = useRejectAccessRequest()

  return (
    <div className='min-h-screen bg-background'>
      <header className='border-b bg-card'>
        <div className='container mx-auto px-4 py-4'>
          <div className='flex items-center justify-between'>
            <div className='flex items-center gap-3'>
              <div className='p-2 bg-primary/10 rounded-lg'>
                <Shield className='h-6 w-6 text-primary' />
              </div>
              <div>
                <h1 className='text-xl font-bold'>JustTime Admin</h1>
                <p className='text-sm text-muted-foreground'>
                  Panel de Administración
                </p>
              </div>
            </div>
            <div className='flex items-center gap-4'>
              <div className='text-right'>
                <p className='text-sm font-medium'>{user?.username}</p>
                <p className='text-xs text-muted-foreground'>Administrador</p>
              </div>
              <Button variant='outline' size='sm' onClick={logout}>
                <LogOut className='h-4 w-4 mr-2' />
                Salir
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className='container mx-auto px-4 py-8'>
        <Tabs defaultValue='databases' className='space-y-6'>
          <TabsList className='grid w-full grid-cols-2'>
            <TabsTrigger value='databases'>Bases de Datos</TabsTrigger>
            <TabsTrigger value='requests'>Solicitudes</TabsTrigger>
          </TabsList>

          {/* Resources Tab */}
          <TabsContent value='databases' className='space-y-6'>
            <DatabaseForm />
            <DatabaseList />
          </TabsContent>

          {/* Requests Tab */}
          <TabsContent value='requests'>
            <Card>
              <CardHeader>
                <CardTitle>Solicitudes de Acceso</CardTitle>
                <CardDescription>
                  Gestiona las solicitudes de acceso a recursos
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className='space-y-4'>
                  {accessRequests.map(request => (
                    <div
                      key={request.id}
                      className='flex items-center justify-between p-4 border rounded-lg'
                    >
                      <div className='flex-1'>
                        <div className='flex items-center gap-3 mb-2'>
                          <h3 className='font-medium'>{request.reason}</h3>
                          <Badge
                            variant={
                              request.status === 'PENDING'
                                ? 'secondary'
                                : request.status === 'APPROVED'
                                  ? 'default'
                                  : 'destructive'
                            }
                          >
                            {request.status}
                          </Badge>
                        </div>
                        <p className='text-sm text-muted-foreground mb-1'>
                          Recurso: {request.resourceId}
                        </p>
                        <p className='text-xs text-muted-foreground'>
                          Solicitado:{' '}
                          {new Date(request.requestAt).toLocaleDateString()}
                        </p>
                      </div>
                      {request.status === 'PENDING' && (
                        <div className='flex items-center gap-2'>
                          <Button
                            size='sm'
                            onClick={() =>
                              approveAccessRequest.mutate({
                                id: request.id,
                                adminId: user?.id || ''
                              })
                            }
                          >
                            Aprobar
                          </Button>
                          <Button
                            variant='destructive'
                            size='sm'
                            onClick={() =>
                              rejectAccessRequest.mutate({
                                id: request.id,
                                adminId: user?.id || ''
                              })
                            }
                          >
                            Denegar
                          </Button>
                        </div>
                      )}
                    </div>
                  ))}
                  {accessRequests.length === 0 && (
                    <div className='text-center py-8 text-muted-foreground'>
                      No hay solicitudes de acceso
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}
