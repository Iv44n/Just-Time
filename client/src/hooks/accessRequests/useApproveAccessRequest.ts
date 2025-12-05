import { QUERY_KEYS } from '@/config/constants'
import { approveAccessRequest } from '@/lib/api'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export function useApproveAccessRequest() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: approveAccessRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [QUERY_KEYS.ACCESS_REQUESTS]
      })
    }
  })
}
