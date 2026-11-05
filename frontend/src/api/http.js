const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export class ApiError extends Error {
  constructor(mensagem, status, detalhes = []) {
    super(mensagem)
    this.status = status
    this.detalhes = detalhes
  }
}

let aoExpirarSessao = () => {}

/** O AuthContext registra aqui o que fazer quando o token expira (HTTP 401). */
export function definirAoExpirarSessao(fn) {
  aoExpirarSessao = fn
}

export async function requisicao(caminho, { metodo = 'GET', corpo, autenticado = true } = {}) {
  const headers = {}
  if (corpo !== undefined) headers['Content-Type'] = 'application/json'

  const token = localStorage.getItem('token')
  if (autenticado && token) headers.Authorization = `Bearer ${token}`

  let resposta
  try {
    resposta = await fetch(BASE_URL + caminho, {
      method: metodo,
      headers,
      body: corpo !== undefined ? JSON.stringify(corpo) : undefined,
    })
  } catch {
    throw new ApiError('Não foi possível conectar ao servidor. Verifique se o back-end está em execução.', 0)
  }

  if (resposta.status === 204) return null

  let dados = null
  try {
    const texto = await resposta.text()
    dados = texto ? JSON.parse(texto) : null
  } catch {
    dados = null
  }

  if (!resposta.ok) {
    if (resposta.status === 401 && autenticado && token) aoExpirarSessao()
    const padrao =
      resposta.status === 403 ? 'Você não tem permissão para esta operação.' : 'Ocorreu um erro inesperado.'
    throw new ApiError(dados?.mensagem || padrao, resposta.status, dados?.detalhes || [])
  }
  return dados
}
