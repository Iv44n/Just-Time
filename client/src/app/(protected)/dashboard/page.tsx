'use client'
import { Button } from '@/components/ui/button'
import {
  CheckCircle,
  Clock,
  Database,
  LogOut,
  Shield,
  Terminal,
  XCircle
} from 'lucide-react'
import { useSignOut } from '@/hooks/useSignOut'
import { useAuth } from '@/hooks/useAuth'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

import { useState } from 'react'
import type { Resource } from '@/types/resource'
import { useRouter } from 'next/navigation'
import { useResources } from '@/hooks/resources'
import {
  useCreateAccessRequest,
  useAccessRequestByUserId
} from '@/hooks/accessRequests'

export default function Dashboard() {
  const router = useRouter()
  const { user } = useAuth()
  const { logout } = useSignOut()
  const { resources } = useResources()
  const { accessRequestsByUserId } = useAccessRequestByUserId({
    id: user?.id ?? ''
  })
  const [showRequestModal, setShowRequestModal] = useState(false)
  const [selectedResource, setSelectedResource] = useState<Resource | null>(
    null
  )
  const [reason, setReason] = useState('')
  const [requestedHours, setRequestedHours] = useState(1)
  const requestAccess = useCreateAccessRequest()

  const requestAccessSubmit = async () => {
    if (!selectedResource || !user?.id) return

    await requestAccess.mutateAsync({
      userId: user.id,
      resourceId: selectedResource.id,
      reason,
      requestedHours
    })
    setShowRequestModal(false)
  }

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
                <h1 className='text-xl font-bold'>Just Time</h1>
                <p className='text-sm text-muted-foreground'>
                  Panel de usuario
                </p>
              </div>
            </div>
            <div className='flex items-center gap-4'>
              <div className='text-right'>
                <p className='text-sm font-medium'>{user?.username}</p>
                <p className='text-xs text-muted-foreground'>Usuario</p>
              </div>
              <Button variant='outline' size='sm' onClick={logout}>
                <LogOut className='h-4 w-4 mr-2' />
                Salir
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className='container mx-auto px-4 py-8'>
        <Tabs defaultValue='databases' className='space-y-6'>
          <TabsList className='grid w-full grid-cols-2'>
            <TabsTrigger value='databases'>Bases de Datos</TabsTrigger>
            <TabsTrigger value='requests'>Mis Solicitudes</TabsTrigger>
          </TabsList>

          {/* Databases Tab */}
          <TabsContent value='databases' className='space-y-6'>
            <Card>
              <CardHeader>
                <CardTitle>Bases de Datos Disponibles</CardTitle>
                <CardDescription>
                  Selecciona una base de datos y solicita acceso
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className='space-y-2'>
                  {resources?.map(resource => {
                    const accessStatus = resource.requestStatus
                    const approvedRequest = accessStatus === 'APPROVED'

                    return (
                      <div
                        key={resource.id}
                        className='flex items-center justify-between p-4 border rounded-lg hover:bg-accent/50 transition-colors'
                      >
                        <div className='flex items-center gap-3'>
                          <Database className='h-5 w-5 text-muted-foreground' />
                          <div>
                            <h3 className='font-medium'>{resource.name}</h3>
                            <p className='text-xs text-muted-foreground'>
                              Creado:{' '}
                              {new Date(
                                resource.createdAt
                              ).toLocaleDateString()}
                            </p>
                          </div>
                          {accessStatus && (
                            <Badge
                              variant={
                                accessStatus === 'APPROVED'
                                  ? 'default'
                                  : accessStatus === 'PENDING'
                                    ? 'secondary'
                                    : 'destructive'
                              }
                              className='ml-2'
                            >
                              {accessStatus === 'APPROVED'
                                ? 'Aprobado'
                                : accessStatus === 'PENDING'
                                  ? 'Pendiente'
                                  : 'Denegado'}
                            </Badge>
                          )}
                        </div>
                        <div className='flex items-center gap-2'>
                          {accessStatus === 'APPROVED' && approvedRequest ? (
                            <Button
                              size='sm'
                              onClick={() =>
                                router.push(
                                  `/dashboard/sql/${resource.requestId}`
                                )
                              }
                            >
                              <Terminal className='h-4 w-4 mr-2' />
                              Ejecutar SQL
                            </Button>
                          ) : accessStatus === 'PENDING' ? (
                            <Button variant='outline' size='sm' disabled>
                              <Clock className='h-4 w-4 mr-2' />
                              Pendiente
                            </Button>
                          ) : (
                            <Button
                              size='sm'
                              onClick={() => {
                                setSelectedResource(resource)
                                setShowRequestModal(true)
                              }}
                            >
                              Solicitar Acceso
                            </Button>
                          )}
                        </div>
                      </div>
                    )
                  })}
                  {resources?.length === 0 && (
                    <div className='text-center py-8 text-muted-foreground'>
                      No hay bases de datos disponibles en este momento
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
            {/* Modal de Solicitud */}
            {showRequestModal && selectedResource && (
              <div className='fixed inset-0 bg-black/50 flex items-center justify-center z-50'>
                <Card className='w-full max-w-md mx-4'>
                  <CardHeader>
                    <CardTitle>Solicitar Acceso</CardTitle>
                    <CardDescription>
                      Confirma tu solicitud de acceso a esta base de datos
                    </CardDescription>
                  </CardHeader>

                  <CardContent className='space-y-4'>
                    {/* Información del recurso */}
                    <div className='p-4 bg-muted rounded-lg'>
                      <div className='flex items-center gap-3'>
                        <Database className='h-8 w-8 text-primary' />
                        <div>
                          <p className='font-medium'>{selectedResource.name}</p>
                        </div>
                      </div>
                    </div>

                    {/* Reason */}
                    <div>
                      <label className='text-sm font-medium' htmlFor='reason'>
                        Razón
                      </label>
                      <input
                        id='reason'
                        className='w-full border p-2 rounded mt-1'
                        value={reason}
                        onChange={e => setReason(e.target.value)}
                        placeholder='Acceso a la base de datos'
                      />
                    </div>

                    {/* Hours */}
                    <div>
                      <label className='text-sm font-medium' htmlFor='hours'>
                        Horas solicitadas
                      </label>
                      <input
                        id='hours'
                        type='number'
                        className='w-full border p-2 rounded mt-1'
                        value={requestedHours}
                        onChange={e =>
                          setRequestedHours(Number(e.target.value))
                        }
                        placeholder='1'
                        min={1}
                      />
                    </div>
                  </CardContent>

                  <CardFooter className='flex gap-2'>
                    <Button
                      variant='outline'
                      className='flex-1 bg-transparent'
                      onClick={() => {
                        setShowRequestModal(false)
                        setSelectedResource(null)
                      }}
                    >
                      Cancelar
                    </Button>

                    <Button className='flex-1' onClick={requestAccessSubmit}>
                      Confirmar Solicitud
                    </Button>
                  </CardFooter>
                </Card>
              </div>
            )}
          </TabsContent>

          {/* Requests Tabs */}
          <TabsContent value='requests'>
            <Card>
              <CardHeader>
                <CardTitle>Mis Solicitudes de Acceso</CardTitle>
                <CardDescription>
                  Historial y estado de tus solicitudes
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className='space-y-4'>
                  {accessRequestsByUserId.length > 0 ? (
                    accessRequestsByUserId.map(request => (
                      <div
                        key={request.id}
                        className='flex items-center justify-between p-4 border rounded-lg'
                      >
                        <div className='flex-1'>
                          <div className='flex items-center gap-3 mb-2'>
                            <Database className='h-4 w-4 text-muted-foreground' />
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
                              {request.status === 'APPROVED' && (
                                <CheckCircle className='h-3 w-3 mr-1' />
                              )}
                              {request.status === 'PENDING' && (
                                <Clock className='h-3 w-3 mr-1' />
                              )}
                              {request.status === 'REJECTED' && (
                                <XCircle className='h-3 w-3 mr-1' />
                              )}
                              {request.status === 'APPROVED'
                                ? 'Aprobado'
                                : request.status === 'PENDING'
                                  ? 'Pendiente'
                                  : 'Denegado'}
                            </Badge>
                          </div>
                          <p className='text-xs text-muted-foreground'>
                            Solicitado:{' '}
                            {new Date(request.requestAt).toLocaleString()}
                          </p>
                        </div>
                        {request.status === 'APPROVED' && (
                          <Button
                            size='sm'
                            variant='outline'
                            onClick={() =>
                              router.push(`/dashboard/sql/${request.id}`)
                            }
                          >
                            <Terminal className='h-4 w-4 mr-2' />
                            SQL
                          </Button>
                        )}
                      </div>
                    ))
                  ) : (
                    <div className='text-center py-8 text-muted-foreground'>
                      No has realizado solicitudes aún
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </main>
    </div>
  )
}
