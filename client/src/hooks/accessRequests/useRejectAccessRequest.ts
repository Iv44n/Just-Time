import { QUERY_KEYS } from '@/config/constants'
import { rejectAccessRequest } from '@/lib/api'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export function useRejectAccessRequest() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: rejectAccessRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [QUERY_KEYS.ACCESS_REQUESTS]
      })
    }
  })
}