import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { INICIO_POR_PERFIL } from '../utils/format'

/**
 * Bloqueia o acesso de quem não fez login.
 * Com "perfis", restringe também ao(s) perfil(is) informado(s): CLIENTE, LOCADORA, BANCO.
 * Quem tenta abrir o hub de outro perfil volta para o início do seu próprio hub.
 */
export default function RotaProtegida({ perfis }) {
  const { autenticado, usuario } = useAuth()

  if (!autenticado) return <Navigate to="/login" replace />
  if (perfis && !perfis.includes(usuario.perfil)) {
    return <Navigate to={INICIO_POR_PERFIL[usuario.perfil]} replace />
  }
  return <Outlet />
}
