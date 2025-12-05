import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'

const URLConnectionForm = ({
  connectionUrl,
  setConnectionUrl
}: {
  connectionUrl: string
  setConnectionUrl: (url: string) => void
}) => {
  return (
    <div className='space-y-2 pt-4 border-t'>
      <Label htmlFor='db-url'>URL de Conexión</Label>
      <Input
        id='db-url'
        placeholder='postgresql://user:password@host:5432/database'
        value={connectionUrl}
        onChange={e => setConnectionUrl(e.target.value)}
      />
      <p className='text-xs text-muted-foreground'>
        Formato: postgresql://user:password@host:port/database
      </p>
    </div>
  )
}

export default URLConnectionForm
