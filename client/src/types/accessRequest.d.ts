export interface AccessRequest {
  id: string
  userId: string
  resourceId: string
  reason: string
  requestedHours: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  requestAt: string
  reviewedAt: string | null
  reviewedBy: string | null
}
