import { QUERY_KEYS } from '@/config/constants'
import { createResource } from '@/lib/api'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export const useCreateResource = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createResource,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [QUERY_KEYS.RESOURCES]
      })
    }
  })
}
