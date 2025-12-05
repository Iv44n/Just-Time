import { QUERY_KEYS } from '@/config/constants'
import { getResources } from '@/lib/api'
import { useQuery } from '@tanstack/react-query'

export const useResources = () => {
  const { data, ...rest } = useQuery({
    queryKey: [QUERY_KEYS.RESOURCES],
    queryFn: getResources
  })

  return { resources: data ?? [], ...rest }
}
