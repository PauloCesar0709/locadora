import { requisicao } from './http'

export const authApi = {
  login: (login, senha) => requisicao('/api/auth/login', { metodo: 'POST', corpo: { login, senha }, autenticado: false }),
}

export const clienteApi = {
  cadastrar: (dados) => requisicao('/api/clientes', { metodo: 'POST', corpo: dados, autenticado: false }),
  buscar: (id) => requisicao(`/api/clientes/${id}`),
  atualizar: (id, dados) => requisicao(`/api/clientes/${id}`, { metodo: 'PUT', corpo: dados }),
  excluir: (id) => requisicao(`/api/clientes/${id}`, { metodo: 'DELETE' }),
}

export const agenteApi = {
  cadastrar: (dados) => requisicao('/api/agentes', { metodo: 'POST', corpo: dados, autenticado: false }),
  minhasInformacoes: () => requisicao('/api/agentes/me'),
  atualizar: (dados) => requisicao('/api/agentes/me', { metodo: 'PUT', corpo: dados }),
  listarBancos: () => requisicao('/api/bancos'),
}

export const automovelApi = {
  listar: () => requisicao('/api/automoveis'),
  criar: (dados) => requisicao('/api/automoveis', { metodo: 'POST', corpo: dados }),
  atualizar: (id, dados) => requisicao(`/api/automoveis/${id}`, { metodo: 'PUT', corpo: dados }),
  excluir: (id) => requisicao(`/api/automoveis/${id}`, { metodo: 'DELETE' }),
}

export const ofertaApi = {
  listar: () => requisicao('/api/ofertas'),
  criar: (dados) => requisicao('/api/ofertas', { metodo: 'POST', corpo: dados }),
  atualizar: (id, dados) => requisicao(`/api/ofertas/${id}`, { metodo: 'PUT', corpo: dados }),
  excluir: (id) => requisicao(`/api/ofertas/${id}`, { metodo: 'DELETE' }),
}

export const pedidoApi = {
  listar: () => requisicao('/api/pedidos'),
  buscar: (id) => requisicao(`/api/pedidos/${id}`),
  criar: (dados) => requisicao('/api/pedidos', { metodo: 'POST', corpo: dados }),
  atualizar: (id, dados) => requisicao(`/api/pedidos/${id}`, { metodo: 'PUT', corpo: dados }),
  cancelar: (id) => requisicao(`/api/pedidos/${id}/cancelamento`, { metodo: 'POST' }),
  pagarPorContaPropria: (id) => requisicao(`/api/pedidos/${id}/pagamento-proprio`, { metodo: 'POST' }),
  solicitarCredito: (id, bancoId) =>
    requisicao(`/api/pedidos/${id}/solicitacao-credito`, { metodo: 'POST', corpo: { bancoId } }),
}

export const creditoApi = {
  listar: () => requisicao('/api/creditos'),
  buscar: (id) => requisicao(`/api/creditos/${id}`),
  aprovar: (id, taxaJurosDiaria) =>
    requisicao(`/api/creditos/${id}/aprovacao`, { metodo: 'POST', corpo: { taxaJurosDiaria } }),
  recusar: (id, motivo) => requisicao(`/api/creditos/${id}/recusa`, { metodo: 'POST', corpo: { motivo } }),
}
