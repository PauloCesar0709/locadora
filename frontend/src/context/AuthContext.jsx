import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { authApi } from '../api/servicos'
import { definirAoExpirarSessao } from '../api/http'

const AuthContext = createContext(null)

function lerSessao() {
  try {
    return JSON.parse(localStorage.getItem('sessao')) || null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(lerSessao)

  const sair = useCallback(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('sessao')
    setSessao(null)
  }, [])

  const entrar = useCallback(async (login, senha) => {
    const resposta = await authApi.login(login, senha)
    const dados = { id: resposta.id, nome: resposta.nome, perfil: resposta.perfil }
    localStorage.setItem('token', resposta.token)
    localStorage.setItem('sessao', JSON.stringify(dados))
    setSessao(dados)
    return dados
  }, [])

  /** Atualiza o nome exibido no menu depois que o usuário edita o cadastro. */
  const atualizarNome = useCallback((nome) => {
    setSessao((atual) => {
      if (!atual) return atual
      const nova = { ...atual, nome }
      localStorage.setItem('sessao', JSON.stringify(nova))
      return nova
    })
  }, [])

  useEffect(() => {
    definirAoExpirarSessao(sair)
  }, [sair])

  const valor = useMemo(
    () => ({
      usuario: sessao,
      autenticado: !!sessao,
      ehCliente: sessao?.perfil === 'CLIENTE',
      ehLocadora: sessao?.perfil === 'LOCADORA',
      ehBanco: sessao?.perfil === 'BANCO',
      entrar,
      sair,
      atualizarNome,
    }),
    [sessao, entrar, sair, atualizarNome],
  )

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de AuthProvider')
  return ctx
}
