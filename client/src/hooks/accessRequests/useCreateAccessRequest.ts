import { QUERY_KEYS } from '@/config/constants'
import { createAccessRequest } from '@/lib/api'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export function useCreateAccessRequest() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createAccessRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [QUERY_KEYS.RESOURCES]
      })
    }
  })
}
