import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import AuthFundo from '../../components/AuthFundo'
import Erro from '../../components/Erro'
import { INICIO_POR_PERFIL } from '../../utils/format'

export default function Login() {
  const { autenticado, usuario, entrar } = useAuth()
  const navegar = useNavigate()
  const { state } = useLocation()
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  if (autenticado) return <Navigate to={INICIO_POR_PERFIL[usuario.perfil]} replace />

  async function enviar(evento) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    try {
      const sessao = await entrar(login.trim(), senha)
      navegar(INICIO_POR_PERFIL[sessao.perfil])
    } catch (e) {
      setErro(e)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <AuthFundo>
      <div className="glass stack">
        <h1>Bem-Vindo!</h1>

        {state?.mensagem && <div className="alert alert-success" role="status">{state.mensagem}</div>}
        <Erro erro={erro} />

        <form className="stack" onSubmit={enviar}>
          <div className="field">
            <label className="sr-only" htmlFor="login">Usuário</label>
            <input
              id="login"
              className="input"
              placeholder="Usuário"
              value={login}
              onChange={(e) => setLogin(e.target.value)}
              required
              autoComplete="username"
            />
          </div>
          <div className="field">
            <label className="sr-only" htmlFor="senha">Senha</label>
            <input
              id="senha"
              type="password"
              className="input"
              placeholder="Senha"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>
          <button type="submit" className="btn btn-accent btn-block" disabled={enviando}>
            {enviando ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <p className="auth-rodape">
          Ainda não tem cadastro?
          <br />
          <Link to="/cadastro">Cadastre-se</Link>
        </p>
      </div>
    </AuthFundo>
  )
}
