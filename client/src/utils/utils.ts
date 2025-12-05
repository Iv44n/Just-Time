import type { ResourceCode } from '@/types/resource'

export const getDatabaseTypeLabel = (typeCode: ResourceCode): string => {
  const types: Record<ResourceCode, string> = {
    db: 'Database',
    api: 'API',
    storage: 'Storage'
  }
  return types[typeCode] || 'Unknown'
}
