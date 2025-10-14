import { TabsContent } from '@radix-ui/react-tabs'
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card'
import LoginForm from '@/components/LoginForm'
import RegisterForm from '@/components/RegisterForm'

export default function AuthForm() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Acceso al Sistema</CardTitle>
        <CardDescription>Inicia sesión o crea una cuenta nueva</CardDescription>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue='sign-in' className='space-y-4'>
          <TabsList className='grid w-full grid-cols-2'>
            <TabsTrigger value='sign-in'>Iniciar Sesión</TabsTrigger>
            <TabsTrigger value='sign-up'>Registrarse</TabsTrigger>
          </TabsList>

          <TabsContent value='sign-in'>
            <LoginForm />
          </TabsContent>
          <TabsContent value='sign-up'>
            <RegisterForm />
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}
