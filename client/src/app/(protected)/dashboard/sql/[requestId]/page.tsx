'use client'

import { SqlExecutorView } from '@/components/sql-executor-view'
import { useParams } from 'next/navigation'

export default function SqlExecutorPage() {
  const params = useParams()
  const requestId = params.requestId as string

  return <SqlExecutorView requestId={requestId} />
}
