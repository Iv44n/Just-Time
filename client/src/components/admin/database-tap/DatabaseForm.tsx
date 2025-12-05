import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import ConnectionTypeToggle from './ConnectionTypeToggle'
import DetailedConnectionForm from './DetailedConnectionForm'
import URLConnectionForm from './URLConnectionForm'
import { useState, useMemo } from 'react'
import { useAuth } from '@/hooks/useAuth'
import { useCreateResource } from '@/hooks/resources'
import type {
  CreateResourceData,
  DatabaseEngine,
  DetailedConnection,
  UrlConnection
} from '@/types/resource'

type ConnectionType = 'detailed' | 'url'

const DEFAULT_PORTS: Record<DatabaseEngine, number> = {
  postgresql: 5432,
  mysql: 3306,
  sqlite: 0,
  sqlserver: 1433
}

const DATABASE_ENGINES: { value: DatabaseEngine; label: string }[] = [
  { value: 'postgresql', label: 'PostgreSQL' },
  { value: 'mysql', label: 'MySQL' },
  { value: 'sqlite', label: 'SQLite' },
  { value: 'sqlserver', label: 'SQL Server' }
]

const createDetailedConnection = (
  engine: DatabaseEngine
): DetailedConnection => ({
  type: 'detailed',
  engine,
  host: '',
  port: DEFAULT_PORTS[engine] || 5432,
  database: '',
  username: '',
  password: '',
  sslMode: 'require'
})

const createURLConnection = (engine: DatabaseEngine): UrlConnection => ({
  type: 'url',
  engine,
  connectionUrl: ''
})

const createInitialResource = (userId: string): CreateResourceData => ({
  name: '',
  typeCode: 'db',
  createdBy: userId,
  details: createDetailedConnection('postgresql')
})

const DatabaseForm = () => {
  const { user } = useAuth()
  const createResource = useCreateResource()

  const [connectionType, setConnectionType] =
    useState<ConnectionType>('detailed')
  const [newResource, setNewResource] = useState<CreateResourceData>(() =>
    createInitialResource(user?.id || '')
  )

  const isSubmitting = useMemo(
    () => createResource.isPending,
    [createResource.isPending]
  )

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    createResource.mutate(newResource)
  }

  const handleNameChange = (name: string) => {
    setNewResource(prev => ({ ...prev, name }))
  }

  const handleEngineChange = (engine: DatabaseEngine) => {
    setNewResource(prev => ({
      ...prev,
      details: {
        ...prev.details,
        engine,
        ...(prev.details.type === 'detailed' && {
          port: DEFAULT_PORTS[engine] || 5432
        })
      }
    }))
  }

  const handleConnectionTypeChange = (type: ConnectionType) => {
    setConnectionType(type)
    setNewResource(prev => ({
      ...prev,
      details:
        type === 'detailed'
          ? createDetailedConnection(prev.details.engine)
          : createURLConnection(prev.details.engine)
    }))
  }

  const handleDetailedConnectionChange = (details: DetailedConnection) => {
    setNewResource(prev => ({ ...prev, details }))
  }

  const handleURLChange = (connectionUrl: string) => {
    setNewResource(prev => ({
      ...prev,
      details: {
        type: 'url',
        engine: prev.details.engine,
        connectionUrl
      }
    }))
  }

  return (
    <Card className='w-full'>
      <CardHeader>
        <CardTitle>Agregar Nueva Base de Datos</CardTitle>
        <CardDescription>
          Registra una nueva base de datos usando formulario detallado o URL de
          conexión
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className='space-y-6'>
          <div className='space-y-2'>
            <Label htmlFor='name'>Nombre</Label>
            <Input
              id='name'
              placeholder='Mi Base de Datos'
              value={newResource.name}
              onChange={e => handleNameChange(e.target.value)}
              required
              disabled={isSubmitting}
            />
          </div>

          <div className='space-y-2'>
            <Label htmlFor='engine'>Motor de Base de Datos</Label>
            <select
              id='engine'
              className='w-full px-3 py-2 border rounded-md'
              value={newResource.details.engine}
              onChange={e =>
                handleEngineChange(e.target.value as DatabaseEngine)
              }
              disabled={isSubmitting}
            >
              {DATABASE_ENGINES.map(({ value, label }) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
            </select>
          </div>

          <ConnectionTypeToggle
            type={connectionType}
            onChange={handleConnectionTypeChange}
          />

          {connectionType === 'detailed' ? (
            <DetailedConnectionForm
              details={newResource.details as DetailedConnection}
              onChange={handleDetailedConnectionChange}
            />
          ) : (
            <URLConnectionForm
              connectionUrl={
                (newResource.details as UrlConnection).connectionUrl
              }
              setConnectionUrl={handleURLChange}
            />
          )}

          <Button
            type='submit'
            className='w-full'
            disabled={isSubmitting || !newResource.name.trim()}
          >
            <Plus className='w-4 h-4 mr-2' />
            {isSubmitting ? 'Agregando...' : 'Agregar Base de Datos'}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

export default DatabaseForm
