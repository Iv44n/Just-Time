'use client'

import { useRouter, useParams } from 'next/navigation'
import { ArrowLeft, Copy, Eye, EyeOff, Save, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useState, useEffect } from 'react'

export function DatabaseDetailsView() {
  const router = useRouter()
  const { id } = useParams()
  const [dbId, setDbId] = useState<string | null>(null)
  const [showPassword, setShowPassword] = useState(false)
  const [copied, setCopied] = useState<string | null>(null)
  const [isEditing, setIsEditing] = useState(false)
  const [editedDb, setEditedDb] = useState<any>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (id) {
      setDbId(id as string)
    }
  }, [id])

  // Get databases from localStorage
  const getDatabases = () => {
    const stored = localStorage.getItem('gatekeeper_databases')
    return stored ? JSON.parse(stored) : []
  }

  useEffect(() => {
    if (dbId) {
      setIsLoading(true)
      const databases = getDatabases()
      console.log(databases)
      const db = databases.find((d: any) => d.id === dbId)
      console.log(db)
      if (db) {
        setEditedDb(db)
      }
      setIsLoading(false)
    }
  }, [dbId])

  const db = editedDb

  const handleSaveChanges = () => {
    const databases = getDatabases()
    const updatedDatabases = databases.map((d: any) =>
      d.id === dbId
        ? {
            ...editedDb,
            updatedAt: new Date().toISOString()
          }
        : d
    )
    localStorage.setItem('databases', JSON.stringify(updatedDatabases))
    setIsEditing(false)
  }

  const handleCancel = () => {
    const databases = getDatabases()
    const currentDb = databases.find((d: any) => d.id === dbId)
    if (currentDb) {
      setEditedDb(currentDb)
    }
    setIsEditing(false)
  }

  if (isLoading || !dbId) {
    return (
      <div className='min-h-screen bg-background p-6'>
        <div className='text-center'>
          <p className='text-muted-foreground'>Cargando...</p>
        </div>
      </div>
    )
  }

  if (!db) {
    return (
      <div className='min-h-screen bg-background p-6'>
        <div className='text-center'>
          <h1 className='text-2xl font-bold mb-4'>
            Base de datos no encontrada
          </h1>
          <Button onClick={() => router.back()}>Volver</Button>
        </div>
      </div>
    )
  }

  const copyToClipboard = (text: string, field: string) => {
    navigator.clipboard.writeText(text)
    setCopied(field)
    setTimeout(() => setCopied(null), 2000)
  }

  const getDatabaseTypeLabel = (typeId: number): string => {
    const types: Record<number, string> = {
      1: 'Database',
      2: 'API',
      3: 'Storage'
    }
    return types[typeId] || 'Unknown'
  }

  return (
    <div className='min-h-screen bg-background'>
      <div className='p-6'>
        <Button variant='ghost' onClick={() => router.back()} className='mb-6'>
          <ArrowLeft className='h-4 w-4 mr-2' />
          Volver
        </Button>

        <div className='max-w-3xl mx-auto space-y-6'>
          {/* Header */}
          <Card>
            <CardHeader>
              <div className='flex items-center justify-between'>
                <div>
                  <CardTitle className='text-3xl'>{db.name}</CardTitle>
                  <CardDescription>
                    Detalles y edición de base de datos
                  </CardDescription>
                </div>
                <div className='flex items-center gap-2'>
                  <Badge
                    variant={db.status === 'ACTIVE' ? 'default' : 'secondary'}
                  >
                    {db.status === 'ACTIVE' ? 'ACTIVE' : 'INACTIVE'}
                  </Badge>
                  {!isEditing && (
                    <Button onClick={() => setIsEditing(true)}>Editar</Button>
                  )}
                </div>
              </div>
            </CardHeader>
          </Card>

          {/* Basic Info */}
          <Card>
            <CardHeader>
              <CardTitle>Información General</CardTitle>
            </CardHeader>
            <CardContent className='space-y-4'>
              {isEditing ? (
                <div className='space-y-4'>
                  <div>
                    <Label htmlFor='edit-name'>Nombre</Label>
                    <Input
                      id='edit-name'
                      value={editedDb.name}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          name: e.target.value
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                  <div className='grid grid-cols-2 gap-4'>
                    <div>
                      <label className='text-xs font-medium text-muted-foreground'>
                        Creado
                      </label>
                      <p className='text-sm font-medium mt-1'>
                        {new Date(editedDb.createdAt).toLocaleString()}
                      </p>
                    </div>
                    <div>
                      <label className='text-xs font-medium text-muted-foreground'>
                        Actualizado
                      </label>
                      <p className='text-sm font-medium mt-1'>
                        {new Date(editedDb.updatedAt).toLocaleString()}
                      </p>
                    </div>
                  </div>
                </div>
              ) : (
                <div className='grid grid-cols-2 gap-4'>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Tipo
                    </label>
                    <p className='text-sm font-medium'>
                      {getDatabaseTypeLabel(db.typeId)}
                    </p>
                  </div>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Motor
                    </label>
                    <p className='text-sm font-medium uppercase'>{db.engine}</p>
                  </div>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Creado
                    </label>
                    <p className='text-sm font-medium'>
                      {new Date(db.createdAt).toLocaleString()}
                    </p>
                  </div>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Actualizado
                    </label>
                    <p className='text-sm font-medium'>
                      {new Date(db.updatedAt).toLocaleString()}
                    </p>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Connection Details */}
          <Card>
            <CardHeader>
              <CardTitle>Detalles de Conexión</CardTitle>
            </CardHeader>
            <CardContent className='space-y-4'>
              {db.connectionUrl ? (
                isEditing ? (
                  <div className='space-y-2'>
                    <Label htmlFor='edit-url'>URL de Conexión</Label>
                    <Input
                      id='edit-url'
                      value={editedDb.connectionUrl}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          connectionUrl: e.target.value
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                ) : (
                  <div className='space-y-2'>
                    <label className='text-xs font-medium text-muted-foreground'>
                      URL de Conexión
                    </label>
                    <div className='flex items-center gap-2 p-3 bg-muted rounded-lg'>
                      <code className='text-sm flex-1 break-all'>
                        {db.connectionUrl}
                      </code>
                      <Button
                        size='sm'
                        variant='ghost'
                        onClick={() => copyToClipboard(db.connectionUrl, 'url')}
                      >
                        <Copy className='h-4 w-4' />
                      </Button>
                    </div>
                    {copied === 'url' && (
                      <p className='text-xs text-green-600'>Copiado!</p>
                    )}
                  </div>
                )
              ) : isEditing ? (
                <div className='grid grid-cols-2 gap-4'>
                  <div>
                    <Label htmlFor='edit-host'>Host</Label>
                    <Input
                      id='edit-host'
                      value={editedDb.host}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          host: e.target.value
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                  <div>
                    <Label htmlFor='edit-port'>Puerto</Label>
                    <Input
                      id='edit-port'
                      type='number'
                      value={editedDb.port}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          port: Number.parseInt(e.target.value)
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                  <div>
                    <Label htmlFor='edit-database'>Base de Datos</Label>
                    <Input
                      id='edit-database'
                      value={editedDb.database}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          database: e.target.value
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                  <div>
                    <Label htmlFor='edit-username'>Usuario</Label>
                    <Input
                      id='edit-username'
                      value={editedDb.username}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          username: e.target.value
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                  <div className='col-span-2'>
                    <Label htmlFor='edit-password'>Contraseña</Label>
                    <Input
                      id='edit-password'
                      type={showPassword ? 'text' : 'password'}
                      value={editedDb.password}
                      onChange={e =>
                        setEditedDb((prev: any) => ({
                          ...prev,
                          password: e.target.value
                        }))
                      }
                      className='mt-1'
                    />
                  </div>
                </div>
              ) : (
                <div className='grid grid-cols-2 gap-4'>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Host
                    </label>
                    <div className='flex items-center gap-2 mt-1'>
                      <p className='text-sm font-medium'>{db.host}</p>
                      <Button
                        size='sm'
                        variant='ghost'
                        onClick={() => copyToClipboard(db.host, 'host')}
                      >
                        <Copy className='h-4 w-4' />
                      </Button>
                    </div>
                  </div>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Puerto
                    </label>
                    <p className='text-sm font-medium mt-1'>{db.port}</p>
                  </div>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Base de Datos
                    </label>
                    <div className='flex items-center gap-2 mt-1'>
                      <p className='text-sm font-medium'>{db.database}</p>
                      <Button
                        size='sm'
                        variant='ghost'
                        onClick={() => copyToClipboard(db.database, 'database')}
                      >
                        <Copy className='h-4 w-4' />
                      </Button>
                    </div>
                  </div>
                  <div>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Usuario
                    </label>
                    <div className='flex items-center gap-2 mt-1'>
                      <p className='text-sm font-medium'>{db.username}</p>
                      <Button
                        size='sm'
                        variant='ghost'
                        onClick={() => copyToClipboard(db.username, 'username')}
                      >
                        <Copy className='h-4 w-4' />
                      </Button>
                    </div>
                  </div>
                  <div className='col-span-2'>
                    <label className='text-xs font-medium text-muted-foreground'>
                      Contraseña
                    </label>
                    <div className='flex items-center gap-2 mt-1 p-2 bg-muted rounded-lg'>
                      <p className='text-sm font-medium flex-1'>
                        {showPassword
                          ? db.password
                          : '•'.repeat(db.password.length)}
                      </p>
                      <Button
                        size='sm'
                        variant='ghost'
                        onClick={() => setShowPassword(!showPassword)}
                      >
                        {showPassword ? (
                          <EyeOff className='h-4 w-4' />
                        ) : (
                          <Eye className='h-4 w-4' />
                        )}
                      </Button>
                      <Button
                        size='sm'
                        variant='ghost'
                        onClick={() => copyToClipboard(db.password, 'password')}
                      >
                        <Copy className='h-4 w-4' />
                      </Button>
                    </div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Action Buttons */}
          {isEditing && (
            <div className='flex gap-3 justify-end'>
              <Button variant='outline' onClick={handleCancel}>
                <X className='h-4 w-4 mr-2' />
                Cancelar
              </Button>
              <Button onClick={handleSaveChanges}>
                <Save className='h-4 w-4 mr-2' />
                Guardar Cambios
              </Button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
