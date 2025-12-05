'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import { Textarea } from '@/components/ui/textarea'
import { Input } from '@/components/ui/input'
import {
  ArrowLeft,
  Play,
  Database,
  Key,
  Copy,
  Check,
  Terminal,
  AlertCircle,
  CheckCircle
} from 'lucide-react'
import { AccessRequest } from '@/types/accessRequest'

interface DatabaseResource {
  id: string
  name: string
  engine: string
  host?: string
  port?: number
  database?: string
  username?: string
  connectionUrl?: string
}

interface SqlResult {
  columns: string[]
  rows: Record<string, string | number>[]
  executionTime: number
  rowCount: number
}

export function SqlExecutorView({ resourceId }: { resourceId: string }) {
  const router = useRouter()
  const [access, setAccess] = useState<AccessRequest | null>(null)
  const [database, setDatabase] = useState<DatabaseResource | null>(null)
  const [sqlQuery, setSqlQuery] = useState('')
  const [apiKeyInput, setApiKeyInput] = useState('')
  const [isExecuting, setIsExecuting] = useState(false)
  const [sqlResults, setSqlResults] = useState<SqlResult | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (!resourceId) return

    const loadData = () => {
      const requests = JSON.parse(
        localStorage.getItem('gatekeeper_requests') || '[]'
      )
      const databases = JSON.parse(
        localStorage.getItem('gatekeeper_databases') || '[]'
      )

      const foundAccess = requests.find(
        (r: AccessRequest) => r.id === resourceId && r.status === 'APPROVED'
      )

      if (foundAccess) {
        setAccess(foundAccess)
        const foundDb = databases.find(
          (d: DatabaseResource) => d.id === foundAccess.databaseId
        )
        setDatabase(foundDb || null)
      }

      setIsLoading(false)
    }

    loadData()
  }, [resourceId])

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const executeSQL = () => {
    if (!sqlQuery.trim() || !apiKeyInput || !access) return

    // Validar API key
    if (apiKeyInput !== access.apiKey) {
      setError(
        'API Key inválida. Verifica que estés usando la API Key correcta para esta base de datos.'
      )
      setSqlResults(null)
      return
    }

    setIsExecuting(true)
    setError(null)

    // Simular ejecución SQL
    setTimeout(() => {
      const query = sqlQuery.toLowerCase().trim()

      // Simular diferentes resultados según el tipo de query
      if (query.startsWith('select')) {
        const mockResults: SqlResult = {
          columns: ['id', 'name', 'email', 'created_at'],
          rows: [
            {
              id: 1,
              name: 'John Doe',
              email: 'john@example.com',
              created_at: '2024-01-15'
            },
            {
              id: 2,
              name: 'Jane Smith',
              email: 'jane@example.com',
              created_at: '2024-01-16'
            },
            {
              id: 3,
              name: 'Bob Wilson',
              email: 'bob@example.com',
              created_at: '2024-01-17'
            }
          ],
          executionTime: Math.random() * 100 + 10,
          rowCount: 3
        }
        setSqlResults(mockResults)
      } else if (
        query.startsWith('insert') ||
        query.startsWith('update') ||
        query.startsWith('delete')
      ) {
        const mockResults: SqlResult = {
          columns: ['affected_rows'],
          rows: [{ affected_rows: Math.floor(Math.random() * 5) + 1 }],
          executionTime: Math.random() * 50 + 5,
          rowCount: 1
        }
        setSqlResults(mockResults)
      } else {
        const mockResults: SqlResult = {
          columns: ['result'],
          rows: [{ result: 'Query ejecutada exitosamente' }],
          executionTime: Math.random() * 30 + 5,
          rowCount: 1
        }
        setSqlResults(mockResults)
      }

      setIsExecuting(false)
    }, 1000)
  }

  if (isLoading) {
    return (
      <div className='min-h-screen bg-background flex items-center justify-center'>
        <div className='animate-pulse text-muted-foreground'>Cargando...</div>
      </div>
    )
  }

  if (!access || !database) {
    return (
      <div className='min-h-screen bg-background'>
        <div className='container mx-auto px-4 py-8'>
          <Card>
            <CardContent className='py-12'>
              <div className='text-center'>
                <Database className='h-12 w-12 mx-auto text-muted-foreground mb-4' />
                <h2 className='text-xl font-semibold mb-2'>
                  Acceso no encontrado
                </h2>
                <p className='text-muted-foreground mb-4'>
                  Este acceso no existe o no tienes permisos para usarlo
                </p>
                <Button onClick={() => router.push('/dashboard')}>
                  <ArrowLeft className='h-4 w-4 mr-2' />
                  Volver al Dashboard
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    )
  }

  return (
    <div className='min-h-screen bg-background'>
      {/* Header */}
      <header className='border-b bg-card'>
        <div className='container mx-auto px-4 py-4'>
          <div className='flex items-center justify-between'>
            <div className='flex items-center gap-4'>
              <Button
                variant='ghost'
                size='sm'
                onClick={() => router.push('/dashboard')}
              >
                <ArrowLeft className='h-4 w-4 mr-2' />
                Volver
              </Button>
              <div className='flex items-center gap-2'>
                <Terminal className='h-5 w-5 text-primary' />
                <h1 className='text-xl font-bold'>Ejecutor SQL</h1>
              </div>
            </div>
            <div className='flex items-center gap-2'>
              <Database className='h-4 w-4 text-muted-foreground' />
              <span className='text-sm font-medium'>{database.name}</span>
            </div>
          </div>
        </div>
      </header>

      <main className='container mx-auto px-4 py-8 space-y-6'>
        {/* API Key Info */}
        <Card>
          <CardHeader>
            <CardTitle className='flex items-center gap-2'>
              <Key className='h-5 w-5' />
              Tu API Key
            </CardTitle>
            <CardDescription>
              Usa esta API Key para autenticar tus consultas a {database.name}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className='flex items-center gap-2 p-3 bg-muted rounded-lg'>
              <code className='flex-1 font-mono text-sm break-all text-green-500'>
                {access.apiKey}
              </code>
              <Button
                variant='ghost'
                size='sm'
                onClick={() => copyToClipboard(access.apiKey || '')}
              >
                {copied ? (
                  <Check className='h-4 w-4 text-green-500' />
                ) : (
                  <Copy className='h-4 w-4' />
                )}
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* SQL Executor */}
        <Card>
          <CardHeader>
            <CardTitle>Ejecutar Consulta</CardTitle>
            <CardDescription>
              Ingresa tu API Key y la consulta SQL para ejecutar en{' '}
              {database.name}
            </CardDescription>
          </CardHeader>
          <CardContent className='space-y-4'>
            <div className='space-y-2'>
              <label
                htmlFor='api-key-input'
                className='text-sm font-medium flex items-center gap-2'
              >
                <Key className='h-4 w-4' />
                API Key
              </label>
              <Input
                type='password'
                placeholder='Ingresa tu API key para autenticar'
                value={apiKeyInput}
                onChange={e => setApiKeyInput(e.target.value)}
                className='font-mono'
              />
            </div>

            <div className='space-y-2'>
              <label
                htmlFor='sql-query-input'
                className='text-sm font-medium flex items-center gap-2'
              >
                <Terminal className='h-4 w-4' />
                Consulta SQL
              </label>
              <Textarea
                id='sql-query-input'
                placeholder='SELECT * FROM users WHERE active = true;'
                value={sqlQuery}
                onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) =>
                  setSqlQuery(e.target.value)
                }
                rows={6}
                className='font-mono'
              />
            </div>

            {error && (
              <div className='flex items-start gap-2 p-3 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive text-sm'>
                <AlertCircle className='h-4 w-4 mt-0.5 shrink-0' />
                {error}
              </div>
            )}

            <Button
              onClick={executeSQL}
              disabled={!sqlQuery.trim() || isExecuting || !apiKeyInput}
              className='w-full'
            >
              <Play className='h-4 w-4 mr-2' />
              {isExecuting ? 'Ejecutando...' : 'Ejecutar Consulta'}
            </Button>
          </CardContent>
        </Card>

        {/* SQL Results */}
        {sqlResults && (
          <Card>
            <CardHeader>
              <CardTitle className='flex items-center gap-2'>
                <CheckCircle className='h-5 w-5 text-green-500' />
                Resultados
              </CardTitle>
              <CardDescription>
                {sqlResults.rowCount} fila{sqlResults.rowCount !== 1 ? 's' : ''}{' '}
                en {sqlResults.executionTime.toFixed(2)}ms
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className='overflow-x-auto rounded-lg border'>
                <table className='w-full border-collapse'>
                  <thead>
                    <tr className='bg-muted'>
                      {sqlResults.columns.map(col => (
                        <th
                          key={col}
                          className='text-left p-3 font-medium text-sm border-b'
                        >
                          {col}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {sqlResults.rows.map((row, i) => (
                      <tr
                        key={i}
                        className='hover:bg-muted/50 transition-colors'
                      >
                        {sqlResults.columns.map(col => (
                          <td
                            key={col}
                            className='p-3 text-sm font-mono border-b'
                          >
                            {String(row[col])}
                          </td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>
        )}
      </main>
    </div>
  )
}
