'use client'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useAppDispatch, useAppSelector } from '@/hooks/useBaseRedux'
import { authRegister, clearError } from '@/store/slices/authSlice'
import { useState } from 'react'

export default function RegisterForm() {
  const { error, isLoading } = useAppSelector(state => state.auth)
  const dispatch = useAppDispatch()
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'ROLE_USER'
  })

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    dispatch(clearError())

    const { name, role, ...rest } = formData

    if (role !== 'ROLE_USER' && role !== 'ROLE_ADMIN') {
      console.error('Invalid role selected')
      return
    }

    try {
      await dispatch(authRegister({ username: name, role, ...rest }))
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
        <Label htmlFor='name'>Nombre</Label>
        <Input
          id='name'
          name='name'
          type='text'
          placeholder='Juan Pérez'
          required
          value={formData.name}
          onChange={handleChange}
        />
      </div>
      <div className='space-y-2'>
        <Label htmlFor='email'>Email</Label>
        <Input
          id='email'
          name='email'
          type='email'
          placeholder='juan@empresa.com'
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
      <div className='space-y-2'>
        <Label htmlFor='role'>Tipo de Usuario</Label>
        <select
          id='role'
          name='role'
          defaultValue={formData.role}
          onChange={e =>
            setFormData(prev => ({ ...prev, role: e.target.value }))
          }
          className='w-full p-2 border border-input bg-background rounded-md'
        >
          <option value='ROLE_USER'>Usuario</option>
          <option value='ROLE_ADMIN'>Administrador</option>
        </select>
      </div>
      {error && <p className='text-red-500 text-sm'>{error}</p>}
      <Button type='submit' className='w-full' aria-disabled={isLoading}>
        {isLoading ? 'Registrando usuario' : 'Registrar'}
      </Button>
    </form>
  )
}
