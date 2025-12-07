import apiClient from '@/config/apiClient'
import type { AccessRequest } from '@/types/accessRequest'
import type { CreateResourceData, Resource } from '@/types/resource'
import type { User } from '@/types/user'

export async function loginUser(
  email: string,
  password: string
): Promise<User> {
  return apiClient.post('/auth/login', { email, password })
}

export async function registerUser(
  email: string,
  password: string,
  username: string,
  role: 'ROLE_USER' | 'ROLE_ADMIN'
): Promise<User> {
  return apiClient.post('/auth/register', { email, password, username, role })
}

export async function logoutUser() {
  return apiClient.get('/auth/logout')
}

export async function getCurrentUser(): Promise<User> {
  return apiClient.get('/auth/me')
}

export async function getResources(): Promise<Resource[]> {
  return apiClient.get('/resources')
}

export async function createResource(
  resource: CreateResourceData
): Promise<Resource> {
  return apiClient.post('/resources', resource)
}

export async function deleteResourceById(id: string) {
  return apiClient.delete(`/resources?id=${id}`)
}

interface AccessRequestResponse {
  message: string
  status: number
  access_request: AccessRequest
}

export async function createAccessRequest(params: {
  userId: string
  resourceId: string
  reason: string
  requestedHours: number
}): Promise<AccessRequestResponse> {
  return await apiClient.post('/access-requests', params)
}

export async function getAccessRequests(): Promise<AccessRequest[]> {
  return apiClient.get('/access-requests')
}

interface AccessRequestByIdResponse {
  id: string
  status: AccessRequest['status']
  reason: string
  requestedHours: number
  requestedAt: string
  reviewedAt: string
  resource: {
    id: string
    name: string
    type: string
    status: string
  }
}

export async function getAccessRequestById(
  accessRequestId: string
): Promise<AccessRequestByIdResponse> {
  return apiClient.get(`/access-requests/${accessRequestId}`)
}

export async function getAccessRequestByUserId(
  userId: string
): Promise<AccessRequest[]> {
  return apiClient.get(`/access-requests/user/${userId}`)
}

export async function approveAccessRequest({
  id,
  adminId
}: {
  id: string
  adminId: string
}): Promise<AccessRequest> {
  return apiClient.put(`/access-requests/${id}/approve?adminId=${adminId}`)
}

export async function rejectAccessRequest({
  id,
  adminId
}: {
  id: string
  adminId: string
}): Promise<AccessRequest> {
  return apiClient.put(`/access-requests/${id}/reject?adminId=${adminId}`)
}
