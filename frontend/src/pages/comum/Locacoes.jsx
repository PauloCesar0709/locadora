import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { pedidoApi } from '../../api/servicos'
import { useAuth } from '../../context/AuthContext'
import StatusBadge from '../../components/StatusBadge'
import Erro from '../../components/Erro'
import { data, FORMA_PAGAMENTO, moeda } from '../../utils/format'

/** Lista de locações: o cliente vê as suas; a locadora vê as das suas ofertas. */
export default function Locacoes() {
  const { ehCliente } = useAuth()
  const navegar = useNavigate()
  const [pedidos, setPedidos] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    pedidoApi.listar().then(setPedidos).catch(setErro)
  }, [])

  return (
    <div className="stack">
      <div className="row-between">
        <div>
          <h1>{ehCliente ? 'Minhas locações' : 'Locações'}</h1>
          <p className="muted">
            {ehCliente ? 'Acompanhe o status de cada locação.' : 'Locações feitas a partir das suas ofertas.'}
          </p>
        </div>
        {ehCliente && (
          <button type="button" className="btn btn-accent" onClick={() => navegar('/locacoes/nova')}>
            Nova locação
          </button>
        )}
      </div>

      <Erro erro={erro} />
      {pedidos === null && !erro && <p className="muted">Carregando...</p>}
      {pedidos && pedidos.length === 0 && <div className="card empty">Nenhuma locação por aqui ainda.</div>}

      {pedidos && pedidos.length > 0 && (
        <div className="card table-wrap">
          <table>
            <thead>
              <tr>
                <th>Nº</th>
                {!ehCliente && <th>Cliente</th>}
                <th>Veículo</th>
                <th>Período</th>
                <th>Total</th>
                <th>Pagamento</th>
                <th>Status</th>
                <th><span className="sr-only">Ações</span></th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map((p) => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  {!ehCliente && <td>{p.clienteNome}</td>}
                  <td>
                    {p.oferta.automovel.marca} {p.oferta.automovel.modelo}
                    <div className="small muted">{ehCliente ? p.oferta.locadoraNome : p.oferta.automovel.placa}</div>
                  </td>
                  <td>
                    {data(p.dataInicio)} a {data(p.dataFim)}
                    <div className="small muted">{p.quantidadeDias} diária(s)</div>
                  </td>
                  <td>{moeda(p.valorTotal)}</td>
                  <td className="small">{FORMA_PAGAMENTO[p.formaPagamento]}</td>
                  <td><StatusBadge status={p.status} /></td>
                  <td>
                    <button type="button" className="btn btn-outline btn-sm" onClick={() => navegar(`/locacoes/${p.id}`)}>
                      Ver detalhes
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
