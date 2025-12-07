import { QUERY_KEYS } from '@/config/constants'
import { getAccessRequestById } from '@/lib/api'
import { useQuery } from '@tanstack/react-query'

export const useAccessRequestById = (accessRequestId: string) => {
  const { data, ...rest } = useQuery({
    queryKey: [QUERY_KEYS.ACCESS_REQUESTS, accessRequestId],
    queryFn: () => getAccessRequestById(accessRequestId)
  })

  return { accessRequest: data, ...rest }
}
