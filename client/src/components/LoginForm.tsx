'use client'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { authLogin, clearError } from '@/store/slices/authSlice'
import { useState } from 'react'
import { useAppDispatch, useAppSelector } from '@/hooks/useBaseRedux'

export default function LoginForm() {
  const dispatch = useAppDispatch()
  const { isLoading, error } = useAppSelector(state => state.auth)
  const [formData, setFormData] = useState({ email: '', password: '' })

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    dispatch(clearError())

    try {
      await dispatch(authLogin(formData))
    } catch (error) {
      console.error(error)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  return (
    <form onSubmit={handleSubmit} className='space-y-4'>
      <div className='space-y-2'>
        <Label htmlFor='email'>Correo Electrónico</Label>
        <Input
          id='email'
          name='email'
          type='email'
          placeholder='admin@gatekeeper.com'
          required
          value={formData.email}
          onChange={handleChange}
        />
      </div>
      <div className='space-y-2'>
        <Label htmlFor='password'>Contraseña</Label>
        <Input
          id='password'
          name='password'
          type='password'
          placeholder='••••••••'
          required
          value={formData.password}
          onChange={handleChange}
        />
      </div>
      {error && <p className='text-red-500 text-sm'>{error}</p>}
      <Button type='submit' className='w-full' aria-disabled={isLoading}>
        {isLoading ? 'Iniciando Sesión...' : 'Iniciar Sesión'}
      </Button>
    </form>
  )
}
