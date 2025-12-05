import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import type { DetailedConnection } from '@/types/resource'

const DetailedConnectionForm = ({
  details,
  onChange
}: {
  details: DetailedConnection
  onChange: (updates: DetailedConnection) => void
}) => {
  return (
    <div className='grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t'>
      <div className='space-y-2'>
        <Label htmlFor='db-host'>Host</Label>
        <Input
          id='db-host'
          placeholder='db.ejemplo.com'
          value={details.host}
          required
          onChange={e => onChange({ ...details, host: e.target.value })}
        />
      </div>
      <div className='space-y-2'>
        <Label htmlFor='db-port'>Puerto</Label>
        <Input
          id='db-port'
          type='number'
          placeholder='5432'
          value={Number.isNaN(details.port) ? '' : details.port}
          required
          onChange={e =>
            onChange({ ...details, port: Number.parseInt(e.target.value, 10) })
          }
        />
      </div>
      <div className='space-y-2'>
        <Label htmlFor='db-database'>Base de Datos</Label>
        <Input
          id='db-database'
          placeholder='neondb'
          value={details.database}
          required
          onChange={e => onChange({ ...details, database: e.target.value })}
        />
      </div>
      <div className='space-y-2'>
        <Label htmlFor='db-username'>Usuario</Label>
        <Input
          id='db-username'
          placeholder='owner'
          value={details.username}
          required
          onChange={e => onChange({ ...details, username: e.target.value })}
        />
      </div>
      <div className='space-y-2 md:col-span-2'>
        <Label htmlFor='db-password'>Contraseña</Label>
        <Input
          id='db-password'
          type='password'
          placeholder='••••••••'
          value={details.password}
          required
          onChange={e => onChange({ ...details, password: e.target.value })}
        />
      </div>
    </div>
  )
}

export default DetailedConnectionForm
