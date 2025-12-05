import { QUERY_KEYS } from '@/config/constants'
import { deleteResourceById } from '@/lib/api'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export const useDeleteResource = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: deleteResourceById,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [QUERY_KEYS.RESOURCES]
      })
    }
  })
}
