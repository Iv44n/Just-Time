export type ResourceCode = 'db' | 'api' | 'storage'

export interface Resource {
  id: string
  name: string
  type: {
    id: number
    code: ResourceCode
    description: string
  }
  status: string
  createdBy: string
  createdAt: string
  updatedAt: string

  requestStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | null
  requestId: string | null
}

export type DatabaseEngine = 'postgresql' | 'mysql' | 'sqlite' | 'sqlserver'

export type DetailedConnection = {
  type: 'detailed'
  engine: DatabaseEngine
  host: string
  port: number
  database: string
  username: string
  password: string
  sslMode: string
}

export type UrlConnection = {
  type: 'url'
  engine: DatabaseEngine
  connectionUrl: string
}

export interface CreateResourceData {
  name: string
  typeCode: string
  createdBy: string
  details: DetailedConnection | UrlConnection
}
