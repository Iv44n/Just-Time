import { QUERY_KEYS } from '@/config/constants'
import { getAccessRequests } from '@/lib/api'
import { useQuery } from '@tanstack/react-query'

export const useAccessRequests = () => {
  const { data, ...rest } = useQuery({
    queryKey: [QUERY_KEYS.ACCESS_REQUESTS],
    queryFn: getAccessRequests
  })

  return { accessRequests: data ?? [], ...rest }
}
