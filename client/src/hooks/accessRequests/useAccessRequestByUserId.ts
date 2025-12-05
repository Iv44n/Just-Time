import { QUERY_KEYS } from '@/config/constants'
import { getAccessRequestByUserId } from '@/lib/api'
import { useQuery } from '@tanstack/react-query'

export function useAccessRequestByUserId(user: { id: string }) {
  const { data, ...rest } = useQuery({
    queryKey: [QUERY_KEYS.ACCESS_REQUESTS],
    queryFn: () => getAccessRequestByUserId(user.id),
    enabled: !!user.id
  })

  return {
    accessRequestsByUserId: data ?? [],
    ...rest
  }
}
