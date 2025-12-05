import { useResources } from '@/hooks/resources'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import DatabaseCard from './DatabaseCard'

const DatabaseList = () => {
  const { resources, isLoading, error } = useResources()

  if (isLoading) {
    return <div>Cargando...</div>
  }

  if (error) {
    return <div>Error al cargar las bases de datos</div>
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Bases de Datos Registradas</CardTitle>
        <CardDescription>
          Gestiona las bases de datos disponibles en el sistema
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className='overflow-x-auto'>
          <table className='w-full text-sm'>
            <thead>
              <tr className='border-b'>
                <th className='text-left py-3 px-4 font-medium'>Nombre</th>
                <th className='text-left py-3 px-4 font-medium'>Tipo</th>
                <th className='text-left py-3 px-4 font-medium'>Estado</th>
                <th className='text-left py-3 px-4 font-medium'>Creado</th>
                <th className='text-left py-3 px-4 font-medium'>Actualizado</th>
                <th className='text-center py-3 px-4 font-medium'>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {resources && resources.length > 0 ? (
                resources.map(resource => (
                  <DatabaseCard key={resource.id} resource={resource} />
                ))
              ) : (
                <tr className='text-center py-8 text-muted-foreground'>
                  <td colSpan={6} className='pt-8'>
                    No hay bases de datos registradas
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}

export default DatabaseList
