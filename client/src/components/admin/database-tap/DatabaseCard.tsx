import { Eye, Trash2 } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import type { Resource } from '@/types/resource'
import { useDeleteResource } from '@/hooks/resources'
import Link from 'next/link'
import { getDatabaseTypeLabel } from '@/utils/utils'

const DatabaseCard = ({ resource }: { resource: Resource }) => {
  const deleteResource = useDeleteResource()

  return (
    <tr className='border-b hover:bg-muted/50'>
      <td className='py-3 px-4 font-medium'>{resource.name}</td>
      <td className='py-3 px-4'>
        <Badge variant='outline'>
          {getDatabaseTypeLabel(resource.type.code)}
        </Badge>
      </td>
      <td className='py-3 px-4'>
        <Badge variant={resource.status === 'active' ? 'default' : 'secondary'}>
          {resource.status}
        </Badge>
      </td>
      <td className='py-3 px-4 text-xs text-muted-foreground'>
        {new Date(resource.createdAt).toLocaleDateString()}
      </td>
      <td className='py-3 px-4 text-xs text-muted-foreground'>
        {new Date(resource.updatedAt).toLocaleDateString()}
      </td>
      <td className='py-3 px-4 text-center'>
        <div className='flex justify-center gap-2'>
          <Link href={`/admin/resources/${resource.id}`}>
            <Button variant='outline' size='sm'>
              <Eye className='h-4 w-4' />
            </Button>
          </Link>
          <Button
            variant='destructive'
            size='sm'
            onClick={() => deleteResource.mutate(resource.id)}
          >
            <Trash2 className='h-4 w-4' />
          </Button>
        </div>
      </td>
    </tr>
  )
}

export default DatabaseCard
